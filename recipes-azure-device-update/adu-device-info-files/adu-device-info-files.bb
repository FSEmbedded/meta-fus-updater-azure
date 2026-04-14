# Generates ADU applicability info files (manufacturer, model, version)
# and installs them into the image.
#
# MANUFACTURER          Reported through the Device Information PnP Interface.
# MODEL                 Reported through the Device Information PnP Interface.
# ADU_SOFTWARE_VERSION  Software version read by ADU Client.

SUMMARY = "ADU device information files"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit allarch

do_compile() {
    echo "${MANUFACTURER}" > adu-manufacturer
    echo "${MODEL}" > adu-model
    echo "${ADU_SOFTWARE_VERSION}" > adu-version
}

do_install() {
    install -d ${D}${sysconfdir}
    install -m ugo=r adu-manufacturer ${D}${sysconfdir}/adu-manufacturer
    install -m ugo=r adu-model ${D}${sysconfdir}/adu-model
    install -m ugo=r adu-version ${D}${sysconfdir}/adu-version
}

FILES:${PN} = " \
    ${sysconfdir}/adu-manufacturer \
    ${sysconfdir}/adu-model \
    ${sysconfdir}/adu-version \
"
