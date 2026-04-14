# Generates ADU hardware compatibility info file.
# Used to determine if an update is compatible with this hardware.

SUMMARY = "ADU hardware compatibility file"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit allarch

do_compile() {
    echo -n "${MACHINE} ${HW_REV}" > adu-hw-compat
}

do_install() {
    install -d ${D}${sysconfdir}
    install -m 0444 adu-hw-compat ${D}${sysconfdir}/adu-hw-compat
}

FILES:${PN} = "${sysconfdir}/adu-hw-compat"
