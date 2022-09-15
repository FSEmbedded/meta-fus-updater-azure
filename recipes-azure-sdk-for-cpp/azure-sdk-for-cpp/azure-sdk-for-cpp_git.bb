# Build and install the azure-blob-storage-file-upload-utility

DESCRIPTION = "Microsoft Azure SD for CPP"
AUTHOR = "Microsoft Corporation"
HOMEPAGE = "https://github.com/Azure/azure-sdk-for-cpp"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e74f78882cab57fd1cc4c5482b9a214a"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI = "gitsm://github.com/Azure/azure-sdk-for-cpp.git;branch=main;protocol=https \
           file://0001-opentelemetry-cpp.patch \
           file://0001-set-correct-cpp-version.patch"

SRCREV = "54111348d1914bbedcbc0140976d8d516c0ac52a"
PV = "1.0+git${SRCPV}"

S = "${WORKDIR}/git"

# util-linux for uuid-dev
# libxml2 for libxml2-dev
DEPENDS = "util-linux curl openssl libxml2 opentelemetry-cpp"
RDEPENDS_${PN} = "opentelemetry-cpp"

inherit cmake

FILES_${PN} = " /usr/share/azure-storage-blobs-cpp \
                /usr/share/azure-storage-queues-cpp \
                /usr/share/azure-storage-common-cpp \
                /usr/share/azure-storage-files-shares-cpp \
                /usr/share/azure-security-attestation-cpp \
                /usr/share/azure-security-keyvault-secrets-cpp \
                /usr/share/azure-security-keyvault-certificates-cpp \
                /usr/share/azure-security-keyvault-keys-cpp \
                /usr/share/azure-identity-cpp \
                /usr/share/azure-template-cpp \
                /usr/share/azure-core-cpp \
                /usr/share/azure-core-tracing-opentelemetry-cpp \
                /usr/share/azure-storage-files-datalake-cpp"

BBCLASSEXTEND = "native nativesdk"