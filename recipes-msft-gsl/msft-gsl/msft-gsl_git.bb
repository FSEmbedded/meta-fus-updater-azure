SUMMARY = "Microsoft Guidelines Support Library (GSL)"
DESCRIPTION = "Header-only C++ library from Microsoft implementing the C++ Core Guidelines"
HOMEPAGE = "https://github.com/microsoft/GSL"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=363055e71e77071107ba2bb9a54bd9a7"

SRC_URI = "git://github.com/microsoft/GSL.git;protocol=https;branch=main"
SRCREV = "1995e86d1ad70519465374fb4876c6ef7c9f8c61"

S = "${WORKDIR}/git"

inherit allarch

do_install() {
    install -d ${D}${includedir}/gsl
    cp -r ${S}/include/gsl/* ${D}${includedir}/gsl/
}

# create dev package to install header
FILES:${PN}-dev += "${includedir}/gsl"
ALLOW_EMPTY:${PN} = "1"
