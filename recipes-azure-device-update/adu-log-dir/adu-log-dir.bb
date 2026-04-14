SUMMARY = "ADU temporary log directory"
DESCRIPTION = "Installs tmpfiles.d config to create /tmp/adu for ADU Client log files."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://adu-logs.conf"

inherit allarch

do_install() {
    install -d ${D}${sysconfdir}/tmpfiles.d
    install -m 0644 ${WORKDIR}/adu-logs.conf ${D}${sysconfdir}/tmpfiles.d
}

FILES:${PN} = "${sysconfdir}/tmpfiles.d/adu-logs.conf"
