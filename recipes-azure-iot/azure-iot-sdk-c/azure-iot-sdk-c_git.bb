# Build and install the azure-iot-sdk-c with PnP support.

DESCRIPTION = "Microsoft Azure IoT SDKs and libraries for C"
AUTHOR = "Microsoft Corporation"
HOMEPAGE = "https://github.com/Azure/azure-iot-sdk-c"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4283671594edec4c13aeb073c219237a"

# We pull from master branch in order to get PnP APIs
SRC_URI = "gitsm://github.com/Azure/azure-iot-sdk-c.git;branch=lts_07_2022;protocol=https"

# Same as in Buildroot
SRCREV = "66d2752edba2158d3843f2eccb13375d0daab651"
PV = "1.0+git${SRCREV}"

S = "${WORKDIR}/git"

# util-linux for uuid-dev
DEPENDS = "util-linux curl openssl boost cpprest libproxy msft-gsl"

# This package does not contain a shared library or any runnable software
ALLOW_EMPTY_${PN} = "1"

inherit cmake

# Do not use amqp since it is deprecated.
# Do not build sample code to save build time.
EXTRA_OECMAKE += "-Duse_amqp:BOOL=OFF -Duse_http:BOOL=ON -Duse_mqtt:BOOL=ON -Ddont_use_uploadtoblob:BOOL=ON -Dskip_samples:BOOL=ON -Dbuild_service_client:BOOL=OFF -Dbuild_provisioning_service_client:BOOL=OFF"

sysroot_stage_all_append () {
    sysroot_stage_dir ${D}${exec_prefix}/cmake ${SYSROOT_DESTDIR}${exec_prefix}/cmake
}

FILES_${PN}-dev += "\
    /usr/cmake \
    /usr/cmake/umock_cTargets.cmake \
    /usr/cmake/umock_cConfigVersion.cmake \
    /usr/cmake/umock_cConfig.cmake \
    /usr/cmake/azure_macro_utils_cConfig.cmake \
    /usr/cmake/azure_macro_utils_cTargets.cmake \
    /usr/cmake/azure_macro_utils_cConfigVersion.cmake \
    /usr/cmake/umock_cFunctions.cmake \
    /usr/cmake/umock_cTargets-noconfig.cmake \
"


BBCLASSEXTEND = "native nativesdk"
