require recipes-config/images/fus-image-update-std.bb

DESCRIPTION = "F&S standard azure image"
LICENSE = "MIT"

CORE_IMAGE_EXTRA_INSTALL += " \
    azure-device-update   \
    adu-agent-service   \
"
