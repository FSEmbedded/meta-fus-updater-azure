SUMMARY = "ADU agent systemd service unit"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://deviceupdate-agent.service"

RDEPENDS:${PN} = "azure-device-update deliveryoptimization-agent-service"

inherit allarch systemd features_check

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_SERVICE:${PN} = "deviceupdate-agent.service"

do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/deviceupdate-agent.service ${D}${systemd_system_unitdir}
}

FILES:${PN} = "${systemd_system_unitdir}/deviceupdate-agent.service"
