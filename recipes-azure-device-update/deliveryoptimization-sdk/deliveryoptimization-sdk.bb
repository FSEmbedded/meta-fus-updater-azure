# Build and install the DO Client CPP SDK.

LICENSE = "CLOSED"

SRC_URI = "gitsm://github.com/microsoft/do-client;branch=main;protocol=https"

# Tag v1.1.0
SRCREV = "d71ade6f692dd8bc319ec3228c956517e9b29292"

PV = "1.0+git${SRCPV}"
S = "${WORKDIR}/git"

DEPENDS = "boost cpprest libproxy msft-gsl curl"

inherit cmake

BUILD_TYPE ?= "Release"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=${BUILD_TYPE}"
# Specify build is for the sdk lib
EXTRA_OECMAKE += "-DDO_INCLUDE_SDK=ON"
# Don't build DO tests.
EXTRA_OECMAKE += "-DDO_BUILD_TESTS=OFF"

# cpprest installs its config.cmake file in a non-standard location.
# Tell cmake where to find it.
EXTRA_OECMAKE += "-Dcpprestsdk_DIR=${WORKDIR}/recipe-sysroot/usr/lib/cmake"
BBCLASSEXTEND = "native nativesdk"
