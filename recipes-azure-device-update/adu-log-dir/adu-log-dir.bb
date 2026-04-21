SUMMARY = "ADU temporary log directory"
DESCRIPTION = "Installs tmpfiles.d config to create directory for ADU Client log files."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://adu-logs.conf"

inherit allarch
require includes/adu_paths.inc
require includes/adu_users.inc

do_install() {
    install -d ${D}${sysconfdir}/tmpfiles.d

    sed \
        -e "s|@ADUC_LOG_DIR@|${ADUC_LOG_DIR}|g" \
        -e "s|@ADUUSER@|${ADUUSER}|g" \
        -e "s|@ADUGROUP@|${ADUGROUP}|g" \
        ${WORKDIR}/adu-logs.conf \
        > ${D}${sysconfdir}/tmpfiles.d/adu-logs.conf

    chmod 0644 ${D}${sysconfdir}/tmpfiles.d/adu-logs.conf
}

FILES:${PN} = "${sysconfdir}/tmpfiles.d/adu-logs.conf"
