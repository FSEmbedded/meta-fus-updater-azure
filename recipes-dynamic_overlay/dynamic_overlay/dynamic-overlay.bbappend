# Copyright (C) 2025 F&S Elektronik Systeme GmbH
# Released under the GPLv2 license

require includes/adu_paths.inc

# X.509 certificate store for Azure Device Update
# jsoncpp and libarchive are only needed when x509 is enabled
PACKAGECONFIG[use-x509-cert] = " \
    -DBUILD_X509_CERTIFICATE_STORE_MOUNT=ON \
    -DTARGET_ARCHIV_DIR_PATH=${ADUC_CERT_DIR} \
    -DFUS_AZURE_CONFIGURATION=${ADUC_DU_CONFIG} \
    -DFUS_AZURE_CERT_CERTIFICATE_NAME=example-com.cert.pem \
    -DFUS_AZURE_CERT_KEY_NAME=example-com.key.pem \
    -DPART_NAME_MTD_CERT=Secure \
    -DEMMC_SECURE_PART_BLK_NR=16384 \
    , \
    , \
    jsoncpp libarchive \
"

ENABLE_X509_CERT ??= "1"
PACKAGECONFIG:append = "${@bb.utils.contains('ENABLE_X509_CERT', '1', ' use-x509-cert', '', d)}"

FILES:${PN}:append = "${@bb.utils.contains('PACKAGECONFIG', 'use-x509-cert', \
    ' ${ADUC_CERT_DIR}', '', d)}"
