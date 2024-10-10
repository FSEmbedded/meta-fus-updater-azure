require recipes-config/images/fus-image-update-std.bb

DESCRIPTION = "F&S standard azure image"
LICENSE = "MIT"

FSUP_WKS_FILE ?= "fus-updater-azure-sdcard.wks.in"
FSUP_CERT_NAME_FILE ?= "certs.fs"

CORE_IMAGE_EXTRA_INSTALL += " \
    azure-device-update \
    adu-agent-service \
"

TOOLCHAIN_TARGET_TASK:append  = " boost boost-dev"
TOOLCHAIN_TARGET_TASK:append  = " cpprest cpprest-dev"
TOOLCHAIN_TARGET_TASK:append  = " libproxy libproxy-dev"
TOOLCHAIN_TARGET_TASK:append  = " msft-gsl msft-gsl-dev"
TOOLCHAIN_TARGET_TASK:append  = " azure-iot-sdk-c-dev"
TOOLCHAIN_TARGET_TASK:append  = " deliveryoptimization-agent deliveryoptimization-agent-dev"
TOOLCHAIN_TARGET_TASK:append  = " deliveryoptimization-sdk deliveryoptimization-sdk-dev"
TOOLCHAIN_TARGET_TASK:append  = " curl curl-dev"

IMAGE_INSTALL += " fs-provisioning-client"

# create empty certs.fs file. Needed to create sysimg image
# by wic build process. See default wic configuration file.
create_fake_certsfs () {
    local certfs_filename="${DEPLOY_DIR_IMAGE}/${FSUP_CERT_NAME_FILE}"
    if [ ! -f "${certfs_filename}" ]; then
        echo "secure" > ${certfs_filename}
    fi
}

IMAGE_PREPROCESS_COMMAND += "create_fake_certsfs; "

fsup_certs_clean () {
    # remove fsupdate template
    rm -rf "${DEPLOY_DIR_IMAGE}/${FSUP_CERT_NAME_FILE}"
}

# extend do_clean function to remove certs.fs file
do_clean:append () {
    # call fsup_certs_clean function
    bb.build.exec_func('fsup_certs_clean', d)
}

