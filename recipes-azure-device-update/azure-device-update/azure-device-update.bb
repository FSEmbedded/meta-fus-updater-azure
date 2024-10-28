# Build and install our ADU sample code.

# Environment variables that can be used to configure the behavior of this recipe.
# ADUC_GIT_URL          Changes the URL of github repository that ADU code is pulled from.
#                           Default: git://github.com/Azure/iot-hub-device-update
#
# ADUC_GIT_BRANCH       Changes the branch that ADU code is pulled from.
#                           Default: develop
#
# ADU_GIT_COMMIT        Changes to the commit from which to checkout the adu code.
#
# BUILD_TYPE            Changes the type of build produced by this recipe.
#                       Valid values are Debug, Release, RelWithDebInfo, and MinRelSize.
#                       These values are the same as the CMAKE_BUILD_TYPE variable.
# Copyright (C) 2024 F&S Elektronik Systeme GmbH
# Released under the GPLv2 license
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"
DESCRIPTION = "azure-device-update"

ADU_GIT_BRANCH ?= "master"
ADU_SRC_URI ?= ""
SRC_URI = "${ADU_SRC_URI};protocol=https;branch=${ADU_GIT_BRANCH}"

ADU_GIT_COMMIT ?= ""

SRC_URI[sha256sum] ?= ""

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRCREV = "${ADU_GIT_COMMIT}"

PV = "1.0+git${SRCPV}"
S = "${WORKDIR}/git"

# ADUC depends on azure-iot-sdk-c, azure-sdk-for-cpp DO Agent SDK, and curl
DEPENDS = "fs-updater-cli azure-iot-sdk-c azure-blob-storage-file-upload-utility deliveryoptimization-agent curl deliveryoptimization-sdk"
RDEPENDS:${PN} += "bash adu-pub-key adu-log-dir deliveryoptimization-agent-service curl openssl-bin nss ca-certificates"

inherit cmake useradd

BUILD_TYPE ?= "Release"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=${BUILD_TYPE}"
# Don't treat warnings as errors.
EXTRA_OECMAKE += "-DADUC_WARNINGS_AS_ERRORS=OFF"
# Build the non-simulator (real) version of the client.
EXTRA_OECMAKE += "-DADUC_PLATFORM_LAYER=linux"
# Integrate with SWUpdate as the installer
EXTRA_OECMAKE += "-DADUC_CONTENT_HANDLERS:STRING='fus/update'"
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
# EXTRA_OECMAKE += "-DADUC_LIBRARY_DIR=${STAGING_DIR_TARGET}"

EXTRA_OECMAKE += "-DUPDATER_CLI_FULL_CMD='/usr/sbin/fs-updater'"
EXTRA_OECMAKE += "-DADUC_DOWNLOADS_FOLDER:STRING=/tmp/adu"
EXTRA_OECMAKE += "-DADUC_BUILD_PACKAGES:BOOL=false"
EXTRA_OECMAKE += "-Duse_ms_default_handler=ON"
EXTRA_OECMAKE += "-Duse_fsup_app_handler=OFF"
EXTRA_OECMAKE += "-Duse_fsup_fw_handler=OFF"
EXTRA_OECMAKE += "-Duse_fsup_update_handler=ON"

# bash - for running shell scripts for install.
# adu-pub-key - to install public key for update package verification.
# adu-log-dir - to create the temporary log directory in the image.
# deliveryoptimization-agent-service - to install the delivery optimization agent for downloads.

ADUC_DATA_DIR ?= "/var/lib/adu"
ADUC_EXTENSIONS_DIR ?= "${ADUC_DATA_DIR}/extensions"
ADUC_EXTENSIONS_INSTALL_DIR ?= "${ADUC_EXTENSIONS_DIR}/sources"
ADUC_COMPONENT_ENUMERATOR_EXTENSION_DIR ?= "${ADUC_EXTENSIONS_DIR}/component_enumerator"
ADUC_CONTENT_DOWNLOADER_EXTENSION_DIR ?= "${ADUC_EXTENSIONS_DIR}/content_downloader"
ADUC_UPDATE_CONTENT_HANDLER_EXTENSION_DIR ?= "${ADUC_EXTENSIONS_DIR}/update_content_handlers"
ADUC_DOWNLOAD_HANDLER_EXTENSION_DIR ?= "${ADUC_EXTENSIONS_DIR}/download_handlers"
ADUC_DOWNLOADS_DIR ?= "${ADUC_DATA_DIR}/downloads"
#ADUC_DOWNLOADS_DIR="/tmp/adu"
ADUC_DOWNLOADS_FOLDER ?= "${ADUC_DOWNLOADS_DIR}"

ADUC_LOG_DIR ?= "/adu/logs"
ADUC_CONF_DIR ?= "/adu"


ADUUSER = "adu"
ADUGROUP = "adu"
DOUSER = "do"
DOGROUP = "do"

USERADD_PACKAGES = "${PN}"

GROUPADD_PARAM:${PN} = "\
    --gid 800 --system adu ; \
    --gid 801 --system do ; \
    "

# USERADD_PARAM specifies command line options to pass to the
# useradd command. Multiple users can be created by separating
# the commands with a semicolon.
# Here we'll create 'adu' user, and 'do' user.
# To download the update payload file, 'adu' user must be a member of 'do' group.
# To save downloaded file into 'adu' downloads directory, 'do' user must be a member of 'adu' group.
USERADD_PARAM:${PN} = "\
    --uid 800 --system -g ${ADUGROUP} -G ${DOGROUP} --no-create-home --shell /bin/false ${ADUUSER} ; \
    --uid 801 --system -g ${DOGROUP} -G ${ADUGROUP} --no-create-home --shell /bin/false ${DOUSER} ; \
    "

do_compile[depends] += "azure-iot-sdk-c:do_prepare_recipe_sysroot"
do_compile[depends] += "azure-sdk-for-cpp:do_prepare_recipe_sysroot"

do_install:append() {
    #create ADUC_DATA_DIR
    install -d ${D}${ADUC_DATA_DIR}
    chgrp ${ADUGROUP} ${D}${ADUC_DATA_DIR}
    chown ${ADUUSER}:${ADUGROUP} ${D}${ADUC_DATA_DIR}
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

    #create ADUC_CONF_DIR
    install -d ${D}${ADUC_CONF_DIR}
    chown root:${ADUGROUP} ${D}${ADUC_CONF_DIR}
    chmod 0750 ${D}${ADUC_CONF_DIR}

    #create ADUC_LOG_DIR
    install -d ${D}${ADUC_LOG_DIR}
    chown ${ADUUSER}:${ADUGROUP} ${D}${ADUC_LOG_DIR}
    chmod 0774 ${D}${ADUC_LOG_DIR}

    install -m 0550 ${S}/src/adu-shell/scripts/adu-swupdate.sh ${D}${bindir}
    chown ${ADUUSER}:${ADUGROUP} ${D}${bindir}/adu-swupdate.sh

    #set owner for adu-shell
    chmod 0550 ${D}${bindir}/adu-shell
    chown root:${ADUGROUP} ${D}${bindir}/adu-shell

    #set S UID for adu-shell
    chmod u+s ${D}${bindir}/adu-shell
    # remove deployment from project deamons
    # use adu-service recipe
    rm -rf ${D}/usr/lib
    # copy scripts to create manifest files
    install -d ${WORKDIR}/iot_hub_scripts
    cp -rf ${S}/tools/AduCmdlets/*.* ${WORKDIR}/iot_hub_scripts/
}

do_deploy() {
    mkdir -p ${DEPLOY_DIR_IMAGE}/iot_hub_scripts
    # copy fs-provisioning dir to deploy directory
    cp -rf ${WORKDIR}/iot_hub_scripts/*.* ${DEPLOY_DIR_IMAGE}/iot_hub_scripts/
}

addtask deploy after do_install

#We don't want the library file hashes to change between do_image -> do_package,
#otherwise the stored json hashes will be incorrect
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

#
# A helper function that registers the required agent's extensions.
#
fakeroot python do_registerAgentExtensions() {

    try:
        workDir = d.getVar("D")
        extensionInstallDir = d.getVar("ADUC_EXTENSIONS_INSTALL_DIR")
        updateContentRegistrationDirectory = d.getVar("ADUC_UPDATE_CONTENT_HANDLER_EXTENSION_DIR")
        contentDownloaderRegistrationDirectory = d.getVar("ADUC_CONTENT_DOWNLOADER_EXTENSION_DIR")
        downloadHandlerRegistrationDirectory = d.getVar("ADUC_DOWNLOAD_HANDLER_EXTENSION_DIR")

        register_content_handler("fus/update:1", "{}/libfus_update_1.so".format(extensionInstallDir), updateContentRegistrationDirectory, workDir)
        register_content_handler("microsoft/update-manifest", "{}/libmicrosoft_steps_1.so".format(extensionInstallDir), updateContentRegistrationDirectory, workDir)
        register_content_downloader("{}/libdeliveryoptimization_content_downloader.so".format(extensionInstallDir), contentDownloaderRegistrationDirectory, workDir)

    except Exception as ex:
        errorMessage = "Failed to create DU Agent extension registration. An exception of type {0} occurred with message:\n{1} and Arguments:\n{2!r}".format(type(ex).__name__, str(ex), ex.args)
        bb.error(errorMessage)
}

do_registerAgentExtensions[depends] += "virtual/fakeroot-native:do_populate_sysroot"
addtask do_registerAgentExtensions after do_install before do_package

fakeroot do_registerAgentExtensions_permissions(){
    chown -R ${ADUUSER}:${ADUGROUP} ${D}${ADUC_UPDATE_CONTENT_HANDLER_EXTENSION_DIR}
}
do_registerAgentExtensions[depends] += "virtual/fakeroot-native:do_populate_sysroot"
addtask do_registerAgentExtensions_permissions after do_registerAgentExtensions before do_package

FILES:${PN} += "${bindir}/AducIotAgent"
FILES:${PN} += "${bindir}/adu-shell"
FILES:${PN} += "${ADUC_DATA_DIR}/* ${ADUC_LOG_DIR}/* ${ADUC_CONF_DIR}/*"
FILES:${PN} += "${ADUC_EXTENSIONS_DIR}/* ${ADUC_EXTENSIONS_INSTALL_DIR}/* ${ADUC_DOWNLOADS_DIR}/*"
FILES:${PN} += "${ADUC_COMPONENT_ENUMERATOR_EXTENSION_DIR}/* ${ADUC_CONTENT_DOWNLOADER_EXTENSION_DIR}/* ${ADUC_UPDATE_CONTENT_HANDLER_EXTENSION_DIR}/* ${ADUC_DOWNLOAD_HANDLER_EXTENSION_DIR}/*"

def create_handlerRegistration(handlerId, handlerFileInstallPath, handlerExtensionDir, handlerRegistrationFileName, workDir):
    import hashlib
    import os
    import io
    import base64
    import json

    registrationProperties = {"fileName":handlerFileInstallPath}
    handlerExtensionInstallDir = "{}{}".format(workDir, handlerExtensionDir)
    handlerFileWorkingPath = "{}{}".format(workDir, handlerFileInstallPath)
    handlerRegistrationOutputPath = os.path.join(handlerExtensionInstallDir, handlerRegistrationFileName)

    if not os.path.isfile(handlerFileWorkingPath):
        raise ValueError("Cannot generate ADU handler registration, the specified path does not exist: {}".format(handlerFileWorkingPath))

    # Get the file size
    registrationProperties["sizeInBytes"] = os.path.getsize(handlerFileWorkingPath)

    # Calculate the file hash
    with open(handlerFileWorkingPath, "rb") as handler:
        data = handler.read()
        sha256_hash = hashlib.sha256(data)
        base64Hash = base64.b64encode(sha256_hash.digest()).decode("ascii")
        registrationProperties["hashes"] = {"sha256":base64Hash}

    # Add the handler Id if provided
    if handlerId is not None:
        registrationProperties["handlerId"] = handlerId

    # Create any required directories and write the registration content to the registration file
    registrationContent = json.dumps(registrationProperties, indent=4)
    if not os.path.exists(handlerExtensionInstallDir):
        os.makedirs(handlerExtensionInstallDir)
    with open(handlerRegistrationOutputPath, "w") as registration:
        registration.write(registrationContent)

def register_content_handler(handlerId, handlerFileInstallPath, handlerExtensionDir, workDir):
    typedDirectoryName = handlerId.replace("/", "_").replace(":", "_")
    typedHandlerExtensionDir = os.path.join(handlerExtensionDir, typedDirectoryName)
    create_handlerRegistration(handlerId, handlerFileInstallPath, typedHandlerExtensionDir, "content_handler.json", workDir)

def register_content_downloader(handlerFileInstallPath, handlerExtensionDir, workDir):
    create_handlerRegistration(None, handlerFileInstallPath, handlerExtensionDir, "extension.json", workDir)

def register_download_handler(handlerId, handlerFileInstallPath, handlerExtensionDir, workDir):
    typedDirectoryName = handlerId.replace("/", "_").replace(":", "_")
    typedHandlerExtensionDir = os.path.join(handlerExtensionDir, typedDirectoryName)
    create_handlerRegistration(handlerId, handlerFileInstallPath, typedHandlerExtensionDir, "download_handler.json", workDir)
