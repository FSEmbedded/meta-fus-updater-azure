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
inherit useradd
require includes/adu_users.inc

USERADD_PACKAGES = "${PN}"

GROUPADD_PARAM:${PN} = "\
    --gid ${ADU_GID} --system ${ADUGROUP} ; \
    --gid ${DO_GID} --system ${DOGROUP} ; \
    "

# USERADD_PARAM specifies command line options to pass to the
# useradd command. Multiple users can be created by separating
# the commands with a semicolon.
# Here we'll create 'adu' user, and 'do' user.
# To download the update payload file, 'adu' user must be a member of 'do' group.
# To save downloaded file into 'adu' downloads directory, 'do' user must be a member of 'adu' group.
USERADD_PARAM:${PN} = "\
    --uid ${ADU_UID} --system -g ${ADUGROUP} -G ${DOGROUP} --no-create-home --shell /bin/false ${ADUUSER} ; \
    --uid ${DO_UID} --system -g ${DOGROUP} -G ${ADUGROUP} --no-create-home --shell /bin/false ${DOUSER} ; \
    "

BBCLASSEXTEND = "native nativesdk"

do_install() {
    local prov_service_dir_name=fs-provisioning
    local provservice=${WORKDIR}/${prov_service_dir_name}
    local du_config=${ADUC_DU_CONFIG}
    install -d ${D}${ADUC_CONF_DIR}
    install ${WORKDIR}/template-du-config.json ${D}${du_config}
    chown ${ADUUSER}:${ADUGROUP} ${D}${du_config}
    install ${WORKDIR}/du-diagnostics-config.json ${D}${ADUC_CONF_DIR}
    chown ${ADUUSER}:${ADUGROUP} ${D}${ADUC_CONF_DIR}/du-diagnostics-config.json
    # create default du-config.json file
    # should be overlayed by device cert store
    sed -i 's|<x509_store>|\"${ADUC_X509_DIR}\"|g' ${D}${du_config}
    sed -i 's|<x509_cert>|\"\"|g' ${D}${du_config}
    sed -i 's|<x509_key>|\"\"|g' ${D}${du_config}
    sed -i 's|<device_id>|\"\"|g' ${D}${du_config}
    sed -i 's|<iothub_name>|\"\"|g' ${D}${du_config}
    sed -i 's|<iothub_suffix>|\"azure-devices.net\"|g' ${D}${du_config}
    sed -i 's|<connection_type>|\"x509\"|g' ${D}${du_config}
    sed -i 's|<connection_data>|\"\"|g' ${D}${du_config}
    sed -i 's|<name>|\"fs\/fsupdate\"|g' ${D}${du_config}
    sed -i 's|<manufacturer>|\"FUS\"|g' ${D}${du_config}
    sed -i 's|<model>|\"${MACHINE}\"|g' ${D}${du_config}
    sed -i 's|<downloads_folder>|\"${ADUC_DOWNLOADS_DIR}\"|g' ${D}${du_config}
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
