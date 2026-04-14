# Build and install the azure-blob-storage-file-upload-utility

SUMMARY = "Microsoft Azure SDK for C++"
DESCRIPTION = "Microsoft Azure SDK for C++"
AUTHOR = "Microsoft Corporation"
HOMEPAGE = "https://github.com/Azure/azure-sdk-for-cpp"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e74f78882cab57fd1cc4c5482b9a214a"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI = "gitsm://github.com/Azure/azure-sdk-for-cpp.git;branch=main;protocol=https \
           file://0001-opentelemetry-cpp.patch \
           file://0001-set-correct-cpp-version.patch \
           file://0002-fix-gcc13-base64-compile-error.patch"

SRCREV = "54111348d1914bbedcbc0140976d8d516c0ac52a"
PV = "1.0+git${SRCPV}"

S = "${WORKDIR}/git"

# util-linux for uuid-dev
# libxml2 for libxml2-dev
DEPENDS = "util-linux curl openssl libxml2 opentelemetry-cpp"
RDEPENDS:${PN} = "opentelemetry-cpp"

inherit cmake

do_install:append() {
    # TMPDIR/WORKDIR/B entfernen
    find ${D} -type f -name "*Targets.cmake" -print0 | while IFS= read -r -d '' f; do
        sed -i \
            -e "s|${WORKDIR}||g" \
            -e "s|${B}||g" \
            -e "s|${S}||g" \
            "$f"
    done
}

sysroot_stage_all:append () {
    sysroot_stage_dir ${D}${exec_prefix}/cmake ${SYSROOT_DESTDIR}${exec_prefix}/cmake
}

FILES:${PN}-dev += "${exec_prefix}/cmake"

FILES:${PN} = " \
    ${datadir}/azure-storage-blobs-cpp \
    ${datadir}/azure-storage-queues-cpp \
    ${datadir}/azure-storage-common-cpp \
    ${datadir}/azure-storage-files-shares-cpp \
    ${datadir}/azure-security-attestation-cpp \
    ${datadir}/azure-security-keyvault-secrets-cpp \
    ${datadir}/azure-security-keyvault-certificates-cpp \
    ${datadir}/azure-security-keyvault-keys-cpp \
    ${datadir}/azure-identity-cpp \
    ${datadir}/azure-template-cpp \
    ${datadir}/azure-core-cpp \
    ${datadir}/azure-core-tracing-opentelemetry-cpp \
    ${datadir}/azure-storage-files-datalake-cpp \
"

BBCLASSEXTEND = "native nativesdk"
