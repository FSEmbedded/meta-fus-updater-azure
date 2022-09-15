FILES_${PN} += " \
	/ramdisk_cert_store \
"

EXTRA_OECMAKE += " -DTARGET_ADU_DIR_PATH=/adu"
EXTRA_OECMAKE += " -DTARGET_ARCHIV_DIR_PATH=/adu/x509_c"
EXTRA_OECMAKE += " -DSOURCE_ARCHIVE_MTD_FILE_PATH=/tmp/x509_cert_store.tar.bz2"
EXTRA_OECMAKE += " -DSOURCE_ARCHIVE_MMC_FILE_PATH=/rw_fs/root/x509_cert_store.tar.bz2"
EXTRA_OECMAKE += " -DFUS_AZURE_CERT_CERTIFICATE_NAME=example-com.cert.pem"
EXTRA_OECMAKE += " -DFUS_AZURE_CERT_KEY_NAME=example-com.key.pem"