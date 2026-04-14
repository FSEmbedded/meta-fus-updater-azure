# Build and install the azure-iot-sdk-c with PnP support.

DESCRIPTION = "Microsoft Azure IoT SDKs and libraries for C"
AUTHOR = "Microsoft Corporation"
HOMEPAGE = "https://github.com/Azure/azure-iot-sdk-c"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4283671594edec4c13aeb073c219237a"

# We pull from main branch in order to get PnP APIs
SRC_URI = "gitsm://github.com/Azure/azure-iot-sdk-c.git;protocol=https;branch=lts_03_2024"

SRCREV = "3d2e0fbac51b3b97a02c61da77cff90483a88047"
PV = "1.0+git${SRCPV}"


S = "${WORKDIR}/git"

# util-linux for uuid-dev
DEPENDS = "util-linux curl openssl boost cpprest libproxy msft-gsl"

inherit cmake

# Do not use amqp since it is deprecated.
# Do not build sample code to save build time.
# use_http: required uhttp for eis_utils
EXTRA_OECMAKE += "-Duse_amqp:BOOL=OFF -Duse_http:BOOL=ON -Duse_mqtt:BOOL=ON -Ddont_use_uploadtoblob:BOOL=ON -Dskip_samples:BOOL=ON -Dbuild_service_client:BOOL=OFF -Dbuild_provisioning_service_client:BOOL=OFF -Duse_prov_client:BOOL=OFF"

sysroot_stage_all:append() {
    sysroot_stage_dir ${D}${exec_prefix}/cmake ${SYSROOT_DESTDIR}${exec_prefix}/cmake
}

do_install:append() {
   # Clean buildpaths from cmake target files for QA compliance
   if [ -d ${D}${libdir}/cmake ]; then
       find ${D}${libdir}/cmake -name "*Targets*.cmake" -exec \
           sed -i 's|${TMPDIR}[^"]*||g; s|${WORKDIR}[^"]*||g' {} \;
   fi

}

ALLOW_EMPTY:${PN} = "1"

FILES:${PN}-dev += "${exec_prefix}/cmake"
