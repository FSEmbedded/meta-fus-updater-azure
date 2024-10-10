# Build and install the fs-provisioning-client

DESCRIPTION = "F&S Provsioning Client Azure IoT SDKs and libraries for C"
AUTHOR = "F&S Elektronik Systeme GmbH"
HOMEPAGE = "https://github.com/Azure/azure-iot-sdk-c"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4283671594edec4c13aeb073c219237a"

PV = "1.0+git${SRCPV}"
SRCREV = "3d2e0fbac51b3b97a02c61da77cff90483a88047"

# Use azure-iot-sdk-c
SRC_URI = "gitsm://github.com/Azure/azure-iot-sdk-c.git;protocol=https;branch=lts_03_2024"

# add patch to create fus_prov_dps_client
SRC_URI:append = " \
    file://0001-Add-fus_prov_dps_client.patch \
    file://0002-tools-CACerts-Improve-certGen-for-FS-settings.patch \
    file://0003-Improve-certGen-script.patch \
    "

S = "${WORKDIR}/git"

# util-linux for uuid-dev
DEPENDS = "util-linux-libuuid-native openssl-native ca-certificates-native"

inherit cmake native

# Add options to build fus_prov_dps_client
EXTRA_OECMAKE += "-Duse_amqp=OFF -Duse_http=ON -Duse_mqtt=ON -Dskip_samples=ON -Duse_prov_client=ON -Duse_fs_prov_client=ON -Dhsm_type_custom=ON -DBUILD_STATIC_LIBS=ON"

do_install:append () {
    local prov_service_dir_name="${FS_PROVISIONING_SERVICE_DIR_NAME}"
    local provservice=${WORKDIR}/${prov_service_dir_name}
    # add provisioning client
    install -d ${provservice}
    install ${B}/provisioning_client/samples/fus_prov_dps_client/fus_prov_dps_client ${provservice}/
    # copy certificates scripts to use provisioning.sh
    install -d ${provservice}/x509
    cp -rf ${S}/tools/CACertificates/*.* ${provservice}/x509
}

do_deploy() {
    local prov_service_dir_name="${FS_PROVISIONING_SERVICE_DIR_NAME}"
    local provservice=${WORKDIR}/${prov_service_dir_name}
    mkdir -p ${DEPLOY_DIR_IMAGE}/${prov_service_dir_name}
    # copy fs-provisioning dir to deploy directory
    cp -rf ${provservice}/* ${DEPLOY_DIR_IMAGE}/${prov_service_dir_name}
}

addtask deploy after do_install

do_clean[depends] += "fs-provisioning-client:do_clean"
do_clean[cleandirs] += "${DEPLOY_DIR_IMAGE}/${FS_PROVISIONING_SERVICE_DIR_NAME}"
