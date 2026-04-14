SUMMARY = "Delivery Optimization agent systemd service unit"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://deliveryoptimization-agent.service"

RDEPENDS:${PN} = "deliveryoptimization-agent"

inherit allarch systemd features_check

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_SERVICE:${PN} = "deliveryoptimization-agent.service"

do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/deliveryoptimization-agent.service ${D}${systemd_system_unitdir}
}

FILES:${PN} = "${systemd_system_unitdir}/deliveryoptimization-agent.service"
