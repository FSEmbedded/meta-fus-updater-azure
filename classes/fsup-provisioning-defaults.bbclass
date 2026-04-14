# Variables for device certificate creation
# see create_device_certificate
FS_PROVISIONING_UPDATE_DEVICES_SUBDIR ?= "fus-devices"
UPDATE_DEVICES_LIST ?= "dev01 dev02"
FS_PROVISIONING_IOTHUB ?= ""
FS_PROVISIONING_DPS_IDSCOPE ?= ""

# Variables for firmware/application manifest creation
MANIFEST_PROVIDER ?= "FUS"
MANIFEST_UPDATE_NAME ?= "FUS-Update"
MANIFEST_FW_UPDATE_VERSION ?= "1.0"
MANIFEST_APP_UPDATE_VERSION ?= "1.0"
MANIFEST_DEVICE_MOD ?= "fsimx"
