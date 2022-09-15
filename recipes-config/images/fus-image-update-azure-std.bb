require recipes-config/images/fus-image-update-std.bb

DESCRIPTION = "F&S standard azure image"
LICENSE = "MIT"

CORE_IMAGE_EXTRA_INSTALL += " \
    azure-device-update   \
    adu-agent-service \
"

TOOLCHAIN_TARGET_TASK_append  = " boost boost-dev"
TOOLCHAIN_TARGET_TASK_append  = " cpprest cpprest-dev"
TOOLCHAIN_TARGET_TASK_append  = " libproxy libproxy-dev"
TOOLCHAIN_TARGET_TASK_append  = " msft-gsl msft-gsl-dev"
TOOLCHAIN_TARGET_TASK_append  = " azure-iot-sdk-c-dev"
TOOLCHAIN_TARGET_TASK_append  = " deliveryoptimization-agent deliveryoptimization-agent-dev"
TOOLCHAIN_TARGET_TASK_append  = " deliveryoptimization-sdk deliveryoptimization-sdk-dev"
TOOLCHAIN_TARGET_TASK_append  = " curl curl-dev"
