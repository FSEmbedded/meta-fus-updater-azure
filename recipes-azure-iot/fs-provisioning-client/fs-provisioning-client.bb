SUMMARY = "F&S Provisioning Client"
DESCRIPTION = "Installs provisioning scripts and default du-config.json for Azure DU."
AUTHOR = "F&S Elektronik Systeme GmbH"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI = " \
    file://provisioning.sh \
    file://template-du-config.json \
    file://du-diagnostics-config.json \
"

DEPENDS = "fs-provisioning-client-native"

inherit allarch fsup-provisioning-defaults
require includes/adu_paths.inc

BBCLASSEXTEND = "native nativesdk"

do_install() {
    local prov_service_dir_name=fs-provisioning
    local provservice=${WORKDIR}/${prov_service_dir_name}
    install -d ${D}${ADUC_CONF_DIR}
    install ${WORKDIR}/template-du-config.json ${D}${ADUC_CONF_DIR}/du-config.json
    install ${WORKDIR}/du-diagnostics-config.json ${D}${ADUC_CONF_DIR}
    # create default du-config.json file
    # should be overlayed by device cert store
    sed -i 's|<x509_store>|\"${ADUC_X509_DIR}\"|g' ${D}${ADUC_CONF_DIR}/du-config.json
    sed -i 's|<x509_cert>|\"\"|g' ${D}${ADUC_CONF_DIR}/du-config.json
    sed -i 's|<x509_key>|\"\"|g' ${D}${ADUC_CONF_DIR}/du-config.json
    sed -i 's|<device_id>|\"\"|g' ${D}${ADUC_CONF_DIR}/du-config.json
    sed -i 's|<iothub_name>|\"\"|g' ${D}${ADUC_CONF_DIR}/du-config.json
    sed -i 's|<iothub_suffix>|\"azure-devices.net\"|g' ${D}${ADUC_CONF_DIR}/du-config.json
    sed -i 's|<connection_type>|\"x509\"|g' ${D}${ADUC_CONF_DIR}/du-config.json
    sed -i 's|<connection_data>|\"\"|g' ${D}${ADUC_CONF_DIR}/du-config.json
    sed -i 's|<name>|\"fs\/fsupdate\"|g' ${D}${ADUC_CONF_DIR}/du-config.json
    sed -i 's|<manufacturer>|\"FUS\"|g' ${D}${ADUC_CONF_DIR}/du-config.json
    sed -i 's|<model>|\"${MACHINE}\"|g' ${D}${ADUC_CONF_DIR}/du-config.json
    sed -i 's|<downloads_folder>|\"${ADUC_DOWNLOADS_DIR}\"|g' ${D}${ADUC_CONF_DIR}/du-config.json
    install -d ${provservice}
    install ${WORKDIR}/provisioning.sh ${provservice}/
    install ${WORKDIR}/template-du-config.json ${provservice}/
}

do_deploy() {
    local prov_service_dir_name=fs-provisioning
    local provservice=${WORKDIR}/${prov_service_dir_name}
    install -d ${DEPLOY_DIR_IMAGE}/${prov_service_dir_name}
    cp -rf ${provservice}/* ${DEPLOY_DIR_IMAGE}/${prov_service_dir_name}
}

addtask deploy after do_install

FILES:${PN} = "${ADUC_CONF_DIR}"
