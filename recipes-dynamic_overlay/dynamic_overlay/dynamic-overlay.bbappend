FILES:${PN} += " \
	/ramdisk_cert_store \
"

# path to root directory for device update agent
EXTRA_OECMAKE += " -DTARGET_ADU_DIR_PATH=/adu"
# path to x509 certificates
EXTRA_OECMAKE += " -DTARGET_ARCHIV_DIR_PATH=/adu"
# path to cerficates store. currently bz2 tarball required.
EXTRA_OECMAKE += " -DSOURCE_ARCHIVE_MTD_FILE_PATH=/tmp/x509_cert_store.tar.bz2"
# path to cerficates store. currently bz2 tarball required.
EXTRA_OECMAKE += " -DSOURCE_ARCHIVE_MMC_FILE_PATH=/tmp/x509_cert_store.tar.bz2"
# default cerificate name for device update agent
EXTRA_OECMAKE += " -DFUS_AZURE_CERT_CERTIFICATE_NAME=example-com.cert.pem"
# default key name for device update agent
EXTRA_OECMAKE += " -DFUS_AZURE_CERT_KEY_NAME=example-com.key.pem"
# mtd partition name for secure data store
EXTRA_OECMAKE += " -DPART_NAME_MTD_CERT=Secure"

# path to configuration for device update agent
EXTRA_OECMAKE += " -DFUS_AZURE_CONFIGURATION=/adu/du-config.json"
# enable support for certificates storage
EXTRA_OECMAKE += " -DBUILD_X509_CERIFICATE_STORE_MOUNT=ON"
