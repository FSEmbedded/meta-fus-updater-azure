require recipes-config/images/fus-image-update-std.bb

DESCRIPTION = "F&S standard azure image"
LICENSE = "MIT"

inherit fsup-provisioning

FSUP_WKS_FILE ?= "fus-updater-azure-sdcard.wks.in"
FSUP_CERT_NAME_FILE ?= "certs.fs"

# MANUFACTURER will be written to file that is read by the ADU Client.
# This value will be reported through the Device Information PnP interface by the ADU Client.
# This value is used as the namespace of the content and for compatibiltiy checks.
MANUFACTURER ?= "FUS"
# MODEL will also be written to file that is read by the ADU Client.
# This value will be reported through the Device Information PnP interface by the ADU Client.
# This value is used in the name of content and for compatibiltiy checks.
MODEL ?= "IMX8MM"
# ADUC_PRIVATE_KEY is the build host path to the .pem private key file to use to sign the image.
# ADUC_PRIVATE_KEY_PASSWORD is the build host path to the .pass password file for the private key.

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

# Image level user/group configuration.
# Inherit extrausers to make the setting of EXTRA_USERS_PARAMS effective.
INHERIT += "extrausers"

# User / group settings
# The settings are separated by the ; character.
# Each setting is actually a command. The supported commands are useradd,
# groupadd, userdel, groupdel, usermod and groupmod.
EXTRA_USERS_PARAMS = "groupadd --gid 800 adu ; \
 groupadd -r --gid 801 do ; \
 useradd --uid 800 -p '' -r -g adu --no-create-home --shell /bin/false adu ; \
 useradd --uid 801 -p '' -r -g do -G adu --no-create-home --shell /bin/false do ; \
 "
