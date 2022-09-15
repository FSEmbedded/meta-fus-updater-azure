# Build and install our ADU sample code.

# Environment variables that can be used to configure the behavior of this recipe.
# ADUC_GIT_BRANCH       Changes the branch that ADU code is pulled from.
# ADUC_SRC_URI          Changes the URI where the ADU code is pulled from.
#                       This URI follows the Yocto Fetchers syntax.
#                       See https://www.yoctoproject.org/docs/latest/ref-manual/ref-manual.html#var-SRC_URI
# BUILD_TYPE            Changes the type of build produced by this recipe.
#                       Valid values are Debug, Release, RelWithDebInfo, and MinRelSize.
#                       These values are the same as the CMAKE_BUILD_TYPE variable.

LICENSE = "CLOSED"
ALLOW_EMPTY_${PN} = "1"

SRC_URI = "file://fus-device-update-azure.tar.gz"
S = "${WORKDIR}/fus-device-update-azure"

# ADUC depends on azure-iot-sdk-c and DO Agent SDK
DEPENDS = "fs-updater-azure azure-iot-sdk-c azure-blob-storage-file-upload-utility deliveryoptimization-agent curl deliveryoptimization-sdk"
RDEPENDS_${PN} += "bash adu-pub-key adu-device-info-files adu-log-dir deliveryoptimization-agent-service"

inherit cmake useradd

BUILD_TYPE ?= "Debug"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=${BUILD_TYPE}"
# Don't treat warnings as errors.
EXTRA_OECMAKE += "-DADUC_WARNINGS_AS_ERRORS=OFF"
# Build the non-simulator (real) version of the client.
EXTRA_OECMAKE += "-DADUC_PLATFORM_LAYER=linux"
# Integrate with SWUpdate as the installer
EXTRA_OECMAKE += "-DADUC_CONTENT_HANDLERS:STRING='fus/firmware fus/application'"
# Set the path to the manufacturer file
EXTRA_OECMAKE += "-DADUC_MANUFACTURER_FILE=${sysconfdir}/adu-manufacturer"
# Set the path to the model file
EXTRA_OECMAKE += "-DADUC_MODEL_FILE=${sysconfdir}/adu-model"
# Set the path to the version file
EXTRA_OECMAKE += "-DADUC_VERSION_FILE=${sysconfdir}/adu-version"
# Use zlog as the logging library.
EXTRA_OECMAKE += "-DADUC_LOGGING_LIBRARY=zlog"
# Change the log directory.
EXTRA_OECMAKE += "-DADUC_LOG_FOLDER=/adu/logs"
# Enable automatic serivce start
EXTRA_OECMAKE += "-DADUC_INSTALL_DAEMON=ON"
# Do not build unit tests
EXTRA_OECMAKE += "-DADUC_BUILD_UNIT_TESTS:BOOL=false"
# Do not package the source code as DEB or RPM package
EXTRA_OECMAKE += "-DADUC_BUILD_PACKAGES:BOOL=false"
# Do not generate documentation for source code
EXTRA_OECMAKE += "-DADUC_BUILD_DOCUMENTATION:BOOL=false"
# Use /adu directory for configuration.
# The /adu directory is on a seperate partition and is not updated during an OTA update.
EXTRA_OECMAKE += "-DADUC_CONF_FOLDER:STRING=/adu"
# cpprest installs its config.cmake file in a non-standard location.
# Tell cmake where to find it.
EXTRA_OECMAKE += "-Dcpprestsdk_DIR=${WORKDIR}/recipe-sysroot/usr/lib/cmake"
# Using the installed DO SDK include files.
EXTRA_OECMAKE += "-DDOSDK_INCLUDE_DIR=${WORKDIR}/recipe-sysroot/usr/include"
# Set correct sysroot path for the AUDC_LIBRARY_DIR variable
EXTRA_OECMAKE += "-DADUC_LIBRARY_DIR=${STAGING_DIR_TARGET}"

EXTRA_OECMAKE += "-DFIRMWARE_VERSION_CHECK_CMD='/usr/sbin/fs-azure --firmware_version'"
EXTRA_OECMAKE += "-DAPPLICATION_VERSION_CHECK_CMD='/usr/sbin/fs-azure --application_version'"
EXTRA_OECMAKE += "-DADUC_DOWNLOADS_FOLDER:STRING=/tmp/adu"
EXTRA_OECMAKE += "-DADUC_BUILD_PACKAGES:BOOL=false"

# bash - for running shell scripts for install.
# adu-pub-key - to install public key for update package verification.
# adu-device-info-files - to install the device info related files onto the image.
# adu-hw-compat - to install the hardware compatibility file used by swupdate.
# adu-log-dir - to create the temporary log directory in the image.
# deliveryoptimization-agent-service - to install the delivery optimization agent for downloads.

INSANE_SKIP_${PN} += "installed-vs-shipped"

ADUC_EXTENSIONS_DIR = "${ADUC_DATA_DIR}/extensions"
ADUC_EXTENSIONS_INSTALL_DIR = "${ADUC_EXTENSIONS_DIR}/sources"
ADUC_COMPONENT_ENUMERATOR_EXTENSION_DIR = "${ADUC_EXTENSIONS_DIR}/component_enumerator"
ADUC_CONTENT_DOWNLOADER_EXTENSION_DIR = "${ADUC_EXTENSIONS_DIR}/content_downloader"
ADUC_UPDATE_CONTENT_HANDLER_EXTENSION_DIR = "${ADUC_EXTENSIONS_DIR}/update_content_handlers"
ADUC_DOWNLOADS_DIR = "${ADUC_DATA_DIR}/downloads"

ADUC_DATA_DIR = "/var/lib/adu"
ADUC_LOG_DIR = "/adu/logs"
ADUC_CONF_DIR = "/adu"

ADUUSER = "adu"
ADUGROUP = "adu"
DOUSER = "do"
DOGROUP = "do"

PACKAGES =+ "${PN}-adu"

USERADD_PACKAGES = "${PN}-adu"

GROUPADD_PARAM_${PN}-adu = "\
    --gid 800 --system adu ; \
    --gid 801 --system do ; \
    "

# USERADD_PARAM specifies command line options to pass to the
# useradd command. Multiple users can be created by separating
# the commands with a semicolon. Here we'll create adu user:
USERADD_PARAM_${PN}-adu = "\
    --uid 800 --system -g ${ADUGROUP} --home-dir /home/${ADUUSER} --no-create-home --shell /bin/false ${ADUUSER} ; \
    --uid 801 --system -g ${DOGROUP} -G ${ADUGROUP} --home-dir /home/${DOUSER} --no-create-home --shell /bin/false ${DOUSER} ; \
    "

do_install_append() {
    # create ADUC_DATA_DIR
    install -d ${D}${ADUC_DATA_DIR}
    chgrp ${ADUGROUP} ${D}${ADUC_DATA_DIR}
    chmod 0770 ${D}${ADUC_DATA_DIR}

    #create ADUC_EXTENSIONS_DIR
    install -d ${D}${ADUC_EXTENSIONS_DIR}
    chgrp ${ADUGROUP} ${D}${ADUC_EXTENSIONS_DIR}
    chmod 0770 ${D}${ADUC_EXTENSIONS_DIR}

    #create ADUC_EXTENSIONS_INSTALL_DIR
    install -d ${D}${ADUC_EXTENSIONS_INSTALL_DIR}
    chgrp ${ADUGROUP} ${D}${ADUC_EXTENSIONS_INSTALL_DIR}
    chmod 0770 ${D}${ADUC_EXTENSIONS_INSTALL_DIR}

    #create ADUC_COMPONENT_ENUMERATOR_EXTENSION_DIR
    install -d ${D}${ADUC_COMPONENT_ENUMERATOR_EXTENSION_DIR}
    chgrp ${ADUGROUP} ${D}${ADUC_COMPONENT_ENUMERATOR_EXTENSION_DIR}
    chmod 0770 ${D}${ADUC_COMPONENT_ENUMERATOR_EXTENSION_DIR}

    #create ADUC_CONTENT_DOWNLOADER_EXTENSION_DIR
    install -d ${D}${ADUC_CONTENT_DOWNLOADER_EXTENSION_DIR}
    chgrp ${ADUGROUP} ${D}${ADUC_CONTENT_DOWNLOADER_EXTENSION_DIR}
    chmod 0770 ${D}${ADUC_CONTENT_DOWNLOADER_EXTENSION_DIR}

    #create ADUC_UPDATE_CONTENT_HANDLER_EXTENSION_DIR
    install -d ${D}${ADUC_UPDATE_CONTENT_HANDLER_EXTENSION_DIR}
    chgrp ${ADUGROUP} ${D}${ADUC_UPDATE_CONTENT_HANDLER_EXTENSION_DIR}
    chmod 0770 ${D}${ADUC_UPDATE_CONTENT_HANDLER_EXTENSION_DIR}

    #create ADUC_DOWNLOADS_DIR
    install -d ${D}${ADUC_DOWNLOADS_DIR}
    chown ${ADUUSER}:${ADUGROUP} ${D}${ADUC_DOWNLOADS_DIR}
    chmod 0770 ${D}${ADUC_DOWNLOADS_DIR}

    # create ADUC_CONF_DIR
    install -d ${D}${ADUC_CONF_DIR}
    chgrp ${ADUGROUP} ${D}${ADUC_CONF_DIR}
    chmod 0774 ${D}${ADUC_CONF_DIR}

    # create ADUC_LOG_DIR
    install -d ${D}${ADUC_LOG_DIR}
    chgrp ${ADUGROUP} ${D}${ADUC_LOG_DIR}
    chmod 0774 ${D}${ADUC_LOG_DIR}

    #install adu-shell to /usr/lib/adu directory.
    install -d ${D}${libdir}/adu

    install -m 0550 ${S}/src/adu-shell/scripts/adu-swupdate.sh ${D}${libdir}/adu
    chown ${ADUUSER}:${ADUGROUP} ${D}${libdir}/adu

    #set owner for adu-shell
    chmod 0550 ${D}${libdir}/adu/adu-shell
    chown root:${ADUGROUP} ${D}${libdir}/adu/adu-shell

    #set S UID for adu-shell
    chmod u+s ${D}${libdir}/adu/adu-shell

    # Use only until FS-Update implements the run as root function
    # Then the workflow is seperatet like it was intended by MS
    # set SUID for AducIotAgent
    chmod u+s ${D}${bindir}/AducIotAgent
}

FILES_${PN} += "${bindir}/AducIotAgent"
FILES_${PN} += "${libdir}/adu/* ${ADUC_DATA_DIR}/* ${ADUC_LOG_DIR}/* ${ADUC_CONF_DIR}/*"
FILES_${PN} += "${ADUC_EXTENSIONS_DIR}/* ${ADUC_EXTENSIONS_INSTALL_DIR}/* ${ADUC_DOWNLOADS_DIR}/*"
FILES_${PN} += "${ADUC_COMPONENT_ENUMERATOR_EXTENSION_DIR}/* ${ADUC_CONTENT_DOWNLOADER_EXTENSION_DIR}/* ${ADUC_UPDATE_CONTENT_HANDLER_EXTENSION_DIR}/*"
FILES_${PN}-adu += "/home/${ADUUSER}/* /home/$(DOUSER)/*"