DESCRIPTION = "F&S Provsioning Client"
AUTHOR = "F&S Elektronik Systeme GmbH"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"


FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# Scripts and configuration for provisioning service
SRC_URI = " \
    file://provisioning.sh \
    file://template-du-config.json \
    file://du-diagnostics-config.json \
    "

# build fs_prov_client
DEPENDS = "fs-provisioning-client-native"

do_install () {
    local prov_service_dir_name=fs-provisioning
    local provservice=${WORKDIR}/${prov_service_dir_name}
    install -d ${D}
    install -d ${D}/adu
    install ${WORKDIR}/template-du-config.json ${D}/adu/du-config.json
    install ${WORKDIR}/du-diagnostics-config.json ${D}/adu
    # create default du-config.json file
    # should be overlayed by device cert store
    sed -i "s/<x509_store>/\"\/adu\/x509_c\"/g" ${D}/adu/du-config.json
    sed -i "s/<x509_cert>/\"\"/g" ${D}/adu/du-config.json
    sed -i "s/<x509_key>/\"\"/g" ${D}/adu/du-config.json
    sed -i "s/<device_id>/\"\"/g" ${D}/adu/du-config.json
    sed -i "s/<iothub_name>/\"\"/g" ${D}/adu/du-config.json
    sed -i "s/<iothub_suffix>/\"azure-devices.net\"/g" ${D}/adu/du-config.json
    sed -i "s/<connection_type>/\"x509\"/g" ${D}/adu/du-config.json
    sed -i "s/<connection_data>/\"\"/g" ${D}/adu/du-config.json
    sed -i 's/<name>/\"fs\/fsupdate\"/g' ${D}/adu/du-config.json
    sed -i 's/<manufacturer>/\"FUS\"/g' ${D}/adu/du-config.json
    sed -i 's/<model>/\"${MACHINE}\"/g' ${D}/adu/du-config.json
    # add
    install -d ${provservice}
    install ${WORKDIR}/provisioning.sh ${provservice}/
    install ${WORKDIR}/template-du-config.json ${provservice}/
}

do_deploy() {
    local prov_service_dir_name=fs-provisioning
    local provservice=${WORKDIR}/${prov_service_dir_name}
    cp -rf ${provservice}/* ${DEPLOY_DIR_IMAGE}/${prov_service_dir_name}
}

addtask deploy after do_install

BBCLASSEXTEND = "native nativesdk"

inherit allarch

FILES:${PN} += "/adu /adu/du-config.json"
