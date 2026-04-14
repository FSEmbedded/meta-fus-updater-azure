# Generates and copies/installs the public key .pem file
# used to validate the signatures of images.
# Note: ADU reference images are signed with test keys.

SUMMARY = "ADU update package public key"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://passwd.pass \
    file://privateKey.pem \
"

DEPENDS = "openssl-native"

inherit allarch
require includes/adu_paths.inc

ADUC_PRIVATE_KEY ?= "${WORKDIR}/privateKey.pem"
ADUC_PRIVATE_KEY_PASSWORD ?= "${WORKDIR}/passwd.pass"

# Generate the public key file using openssl, private key, and password file.
do_compile() {
    openssl rsa -in ${ADUC_PRIVATE_KEY} -passin file:${ADUC_PRIVATE_KEY_PASSWORD} -out public.pem -outform PEM -pubout
}

do_install() {
    install -d ${D}${ADUC_KEY_DIR}
    install -m 0444 public.pem ${D}${ADUC_KEY_DIR}/public.pem
}

FILES:${PN} = "${ADUC_KEY_DIR}/public.pem"
