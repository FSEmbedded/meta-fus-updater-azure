# Build and install Delivery Optimization Simple Client.

# Environment variables that can be used to configure the behaviour of this recipe.
# DO_SRC_URI            Changes the URI where the DO code is pulled from.
#                       This URI follows the Yocto Fetchers syntax.
#                       See https://www.yoctoproject.org/docs/latest/ref-manual/ref-manual.html#var-SRC_URI
# BUILD_TYPE            Changes the type of build produced by this recipe.
#                       Valid values are Debug, Release, RelWithDebInfo, and MinRelSize.
#                       These values are the same as the CMAKE_BUILD_TYPE variable.

SUMMARY = "Delivery Optimization agent"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "gitsm://github.com/microsoft/do-client;branch=main;protocol=https"

# Tag v1.1.0
SRCREV = "d71ade6f692dd8bc319ec3228c956517e9b29292"

PV = "1.0+git${SRCPV}"
S = "${WORKDIR}/git"

SRC_URI += "file://Findlibproxy.cmake.patch"
SRC_URI += "file://Findglib-2.0.cmake.patch"
SRC_URI += "file://0001-add-std-array-include-in-download-cpp.patch"
SRC_URI += "file://0001-fix-array-incl-in-http_agent-cpp.patch"
SRC_URI += "file://0001-incl-array-in-do-guid-cpp.patch"
SRC_URI += "file://0001-add-glib-incl-paths-client-lite-CMakeLists.txt.patch"

DEPENDS = "boost curl cpprest libproxy msft-gsl glib-2.0"

inherit cmake

BUILD_TYPE ?= "Release"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=${BUILD_TYPE}"
# Don't build DO tests.
EXTRA_OECMAKE += "-DDO_BUILD_TESTS=OFF"
# Specify build is for deliveryoptimization-agent
EXTRA_OECMAKE += "-DDO_INCLUDE_AGENT=ON"

# DO recipes having trouble finding glib-object.h so modify cmake include path.
EXTRA_OECMAKE += "-DCMAKE_INCLUDE_PATH=${WORKDIR}/recipe-sysroot/usr/include/glib-2.0"

# cpprest installs its config.cmake file in a non-standard location.
# Tell cmake where to find it.
EXTRA_OECMAKE += "-Dcpprestsdk_DIR=${WORKDIR}/recipe-sysroot/usr/lib/cmake"
BBCLASSEXTEND = "native nativesdk"
