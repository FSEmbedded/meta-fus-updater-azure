# F&S provisiong functions to create certificates
# and create device on azure iothub

# enviroments to create device certificate(s)
# see create_device_certificate
# directory name
FS_PROVISIONING_SERVICE_DIR_NAME ?="fs-provisioning"
FS_PROVISIONING_UPDATE_DEVICES_SUBDIR ?="fus-devices"
UPDATE_DEVICES_LIST ?="dev01 dev02"
FS_PROVISIONING_IOTHUB ?=""
FS_PROVISIONING_DPS_IDSCOPE ?=""
# enviroments to create application image
APPLICATION_VERSION ?="20241019"
APPLICATION_CONTAINER_NAME ?= "application_container"
# enviroments to create firmware image
FIRMWARE_VERSION ?= "20241019"
# enviroments to create manifests for firmware or application
MANIFEST_PROVIDER ?="FUS"
MANIFEST_UPDATE_NAME ?="FUS-Update"
MANIFEST_FW_UPDATE_VERSION ?="1.0"
MANIFEST_APP_UPDATE_VERSION ?="1.0"
MANIFEST_DEVICE_MOD ?="fsimx"

# add data dir for second rw partition
create_device_certificate() {
    local prov_service_dir_name="${FS_PROVISIONING_SERVICE_DIR_NAME}"
    local update_devices_list="${UPDATE_DEVICES_LIST}"
    local fs_dps_updevices_subdir="${FS_PROVISIONING_UPDATE_DEVICES_SUBDIR}"
    local prov_service_home=${DEPLOY_DIR_IMAGE}/${prov_service_dir_name}
    local fs_dps_idscope="${FS_PROVISIONING_DPS_IDSCOPE}"
    local fs_up_iothub="${FS_PROVISIONING_IOTHUB}"

    # use cert dir from the target rootfs if SSL_CERT_DIR not defined.
    export SSL_CERT_DIR=${SSL_CERT_DIR:-${IMAGE_ROOTFS}/etc/ssl/certs/}
    # use native openssl if other command not defined
    export OPENSSL_CMD=${OPENSSL_CMD:-$(which openssl)}
    # use MANIFEST_PROVIDER, MANIFEST_DEVICE_MOD in provisioning.sh
    export MANIFEST_PROVIDER="${MANIFEST_PROVIDER}"
    export MANIFEST_DEVICE_MOD="${MANIFEST_DEVICE_MOD}"

    # log fsup env. block
    bbdebug 1 ""
    bbdebug 1 "### SSL_CERT_DIR: ${SSL_CERT_DIR}"
    bbdebug 1 "### OPENSSL_CMD: ${OPENSSL_CMD}"
    bbdebug 1 "### prov_service_dir_name: ${prov_service_dir_name}"
    bbdebug 1 "### fs_dps_updevices_subdir: ${fs_dps_updevices_subdir}"
    bbdebug 1 "### update_devices_list: ${update_devices_list}"
    bbdebug 1 "### fs_dps_idscope: ${fs_dps_idscope}"
    bbdebug 1 "### fs_up_iothub: ${fs_up_iothub}"
    bbdebug 1 ""

    ${prov_service_home}/provisioning.sh -g \
    --customer ${prov_service_home}/${fs_dps_updevices_subdir} \
    --home_dir ${prov_service_home}

    if [ -z $fs_dps_idscope ] || [ -z $fs_up_iothub ];
    then
        bbwarn ""
        bbwarn "########################################################################################"
        bbwarn "#### Check DSP IDScope and/or IOTHUB values!                                        ####"
        bbwarn "#### FS_PROVISIONING_DPS_IDSCOPE or FS_PROVISIONING_IOTHUB must be defined!         ####"
        bbwarn "########################################################################################"
        bbwarn ""
    else
        for up_dev in ${update_devices_list}; do
            ${prov_service_home}/provisioning.sh -n \
            --customer ${prov_service_home}/${fs_dps_updevices_subdir} \
            --home_dir ${prov_service_home} \
            --devdir ${up_dev} --devid ${up_dev} --idscope ${fs_dps_idscope} --iothub ${fs_up_iothub}
        done
    fi
}

# create update images
create_update_manifest_images() {
    local iot_du_tools_dir=${DEPLOY_DIR_IMAGE}/iot_hub_scripts
    local manifest_provider=${MANIFEST_PROVIDER}
    local manifest_up_name=${MANIFEST_UPDATE_NAME}
    local manifest_fw_up_version=${MANIFEST_FW_UPDATE_VERSION}
    local manifest_dev_mod=${MANIFEST_DEVICE_MOD}
    local fw_version=${FIRMWARE_VERSION}
    local app_version=${APPLICATION_VERSION}
    local manifest_app_up_version=${MANIFEST_APP_UPDATE_VERSION}
    local now=$(date '+%Y%m%d%H%M%S')
    local fsup_image_dir_name="${FSUP_IMAGE_DIR_NAME}"
    local fsup_images_dir=${DEPLOY_DIR_IMAGE}/${fsup_image_dir_name}

    # create fsupdate images for nand boot device
    if echo "${IMAGE_FSTYPES}" | grep -q "ubifs"; then
        # create manifest for rauc firwmare image
        . ${iot_du_tools_dir}/create-adu-import-manifest.sh -p "${manifest_provider}" \
            -n "${manifest_up_name}" -v "${manifest_fw_up_version}" \
            -c "deviceManufacturer:${manifest_provider}" \
            -c "deviceModel:${manifest_dev_mod}" \
            -h "fus/update:1" \
            -r "installedCriteria:${fw_version}" -r "updateType:firmware" \
            ${DEPLOY_DIR_IMAGE}/rauc_update_nand.artifact > \
                ${DEPLOY_DIR_IMAGE}/${manifest_provider}.${manifest_up_name}-fw.${now}.importmanifest.json

        # create manifest for fs common update
        . ${iot_du_tools_dir}/create-adu-import-manifest.sh -p "${manifest_provider}" \
            -n "${manifest_up_name}" -v "${manifest_fw_up_version}" \
            -c "deviceManufacturer:${manifest_provider}" \
            -c "deviceModel:${manifest_dev_mod}" \
            -h "fus/update:1" \
            -r "installedCriteria:${fw_version}" -r "updateType:common-both" \
            ${fsup_images_dir}/update_nand.fs > \
                ${fsup_images_dir}/${manifest_provider}.${manifest_up_name}-common-update-nand.${now}.importmanifest.json

        # create manifest for common firmware package
        . ${iot_du_tools_dir}/create-adu-import-manifest.sh -p "${manifest_provider}" \
            -n "${manifest_up_name}" -v "${manifest_fw_up_version}" \
            -c "deviceManufacturer:${manifest_provider}" \
            -c "deviceModel:${manifest_dev_mod}" \
            -h "fus/update:1" \
            -r "installedCriteria:${fw_version}" -r "updateType:common-firmware" \
            ${fsup_images_dir}/firmware_nand.fs > \
                ${fsup_images_dir}/${manifest_provider}.${manifest_up_name}-common-fw-nand.${now}.importmanifest.json
    fi

    # create fsupdate images for emmc boot device
    if echo "${IMAGE_FSTYPES}" | grep -q "wic.gz\|wic"; then
        # create manifest for rauc firwmare image
        . ${iot_du_tools_dir}/create-adu-import-manifest.sh -p "${manifest_provider}" \
            -n "${manifest_up_name}" -v "${manifest_fw_up_version}" \
            -c "deviceManufacturer:${manifest_provider}" \
            -c "deviceModel:${manifest_dev_mod}" \
            -h "fus/update:1" \
            -r "installedCriteria:${fw_version}" -r "updateType:firmware" \
            ${DEPLOY_DIR_IMAGE}/rauc_update_emmc.artifact > \
                ${DEPLOY_DIR_IMAGE}/${manifest_provider}.${manifest_up_name}-fw.${now}.importmanifest.json

        # create manifest for fs common update
        . ${iot_du_tools_dir}/create-adu-import-manifest.sh -p "${manifest_provider}" \
            -n "${manifest_up_name}" -v "${manifest_fw_up_version}" \
            -c "deviceManufacturer:${manifest_provider}" \
            -c "deviceModel:${manifest_dev_mod}" \
            -h "fus/update:1" \
            -r "installedCriteria:${fw_version}" -r "updateType:common-both" \
            ${fsup_images_dir}/update_emmc.fs > \
                ${fsup_images_dir}/${manifest_provider}.${manifest_up_name}-common-update-emmc.${now}.importmanifest.json

        # create manifest for common firmware package
        . ${iot_du_tools_dir}/create-adu-import-manifest.sh -p "${manifest_provider}" \
            -n "${manifest_up_name}" -v "${manifest_fw_up_version}" \
            -c "deviceManufacturer:${manifest_provider}" \
            -c "deviceModel:${manifest_dev_mod}" \
            -h "fus/update:1" \
            -r "installedCriteria:${fw_version}" -r "updateType:common-firmware" \
            ${fsup_images_dir}/firmware_emmc.fs > \
                ${fsup_images_dir}/${manifest_provider}.${manifest_up_name}-common-fw-emmc.${now}.importmanifest.json
    fi

    # create manifest for application image
    . ${iot_du_tools_dir}/create-adu-import-manifest.sh -p "${manifest_provider}" \
        -n "${manifest_up_name}" -v "${manifest_app_up_version}" \
        -c "deviceManufacturer:${manifest_provider}" \
        -c "deviceModel:${manifest_dev_mod}" \
        -h "fus/update:1" \
        -r "installedCriteria:${app_version}" -r "updateType:application" \
        ${DEPLOY_DIR_IMAGE}/${APPLICATION_CONTAINER_NAME} > \
            ${DEPLOY_DIR_IMAGE}/${manifest_provider}.${manifest_up_name}-app.${now}.importmanifest.json

    # create manifest for common application package
    . ${iot_du_tools_dir}/create-adu-import-manifest.sh -p "${manifest_provider}" \
        -n "${manifest_up_name}" -v "${manifest_app_up_version}" \
        -c "deviceManufacturer:${manifest_provider}" \
        -c "deviceModel:${manifest_dev_mod}" \
        -h "fus/update:1" \
        -r "installedCriteria:${app_version}" -r "updateType:common-application" \
        ${fsup_images_dir}/application.fs > \
            ${fsup_images_dir}/${manifest_provider}.${manifest_up_name}-common-app.${now}.importmanifest.json
}

IMAGE_POSTPROCESS_COMMAND += "create_update_manifest_images; create_device_certificate; "

# remove all created manifests
fsup_manifest_clean () {
    rm -rf ${DEPLOY_DIR_IMAGE}/${MANIFEST_PROVIDER}*
}

# extend do_clean function to remove all available manifest files
do_clean:append () {
    # call fsup_manifest_clean function
    bb.build.exec_func('fsup_manifest_clean', d)
}
