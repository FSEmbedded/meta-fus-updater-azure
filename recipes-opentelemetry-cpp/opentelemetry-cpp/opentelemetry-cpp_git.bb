DESCRIPTION = "The C++ OpenTelemetry client" 
LICENSE = "Apache-2.0" 
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

DEPENDS = "abseil-cpp curl"
RDEPENDS:${PN} = "abseil-cpp libcurl"

# use release tag v1.16.1
SRCREV = "baecbb95bd63df53e0af16e87bc683967962c5f8"
SRC_URI = "git://github.com/open-telemetry/opentelemetry-cpp;branch=main;protocol=https"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE =  " -DWITH_STL=ON"
EXTRA_OECMAKE += " -DWITH_ABSEIL=ON"
EXTRA_OECMAKE += " -DBUILD_SHARED_LIBS=ON"
EXTRA_OECMAKE += " -DCMAKE_POSITION_INDEPENDENT_CODE=ON"
EXTRA_OECMAKE += " -DBUILD_TESTING=OFF"
EXTRA_OECMAKE += " -DCMAKE_CXX_STANDARD=17"

FILES_${PN}-dev += "${includedir} ${libdir}/cmake ${libdir}/pkgconfig"

SOLIBS = ".so"
FILES_SOLIBSDEV = ""
