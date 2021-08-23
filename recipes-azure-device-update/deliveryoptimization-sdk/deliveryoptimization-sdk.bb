# Build and install the DO Client CPP SDK.

LICENSE = "CLOSED"

SRC_URI = "gitsm://github.com/microsoft/do-client;branch=main"

# Tag v0.7.0
SRCREV = "3f00d1e0f841e6376f1c2852079e19ccc00f8ec4"

PV = "1.0+git${SRCPV}"
S = "${WORKDIR}/git"

DEPENDS = "boost cpprest libproxy msft-gsl"

inherit cmake

BUILD_TYPE ?= "Debug"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=${BUILD_TYPE}"
# Specify build is for the sdk lib
EXTRA_OECMAKE += "-DDO_INCLUDE_SDK=ON"
# Don't build DO tests.
EXTRA_OECMAKE += "-DDO_BUILD_TESTS=OFF"

# cpprest installs its config.cmake file in a non-standard location.
# Tell cmake where to find it.
EXTRA_OECMAKE += "-Dcpprestsdk_DIR=${WORKDIR}/recipe-sysroot/usr/lib/cmake"
BBCLASSEXTEND = "native nativesdk"