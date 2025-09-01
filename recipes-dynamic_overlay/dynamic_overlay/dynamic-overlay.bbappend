# Copyright (C) 2025 F&S Elektronik Systeme GmbH
# Released under the GPLv2 license
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

# enable package config use-x509-cert
ENABLE_X509_CERT ??= "1"
PACKAGECONFIG:append = "${@bb.utils.contains('ENABLE_X509_CERT', '1', ' use-x509-cert', '', d)}"
