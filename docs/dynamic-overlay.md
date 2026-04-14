## Extended configuration for dynamic overlay

The *recipes-dynamic_overlay* extends recipe from *meta-fus-updater* to build support for Azure Cloud.


#### Additional definitions

- **TARGET_ARCHIV_DIR_PATH** is path to x509 certificates.
  Default value "/etc/adu/certs".
- **SOURCE_ARCHIVE_MTD_FILE_PATH** is path to cerficates store.
  Currently bz2 tarball required. Default value is
  "/tmp/x509_cert_store.tar.bz2".
- **SOURCE_ARCHIVE_MMC_FILE_PATH** is path to cerficates store.
  Currently bz2 tarball required. Default value is
  "/tmp/x509_cert_store.tar.bz2".
- **FUS_AZURE_CERT_CERTIFICATE_NAME** is default cerificate name
  for device update agent. Default value is "example-com.cert.pem".
- **FUS_AZURE_CERT_KEY_NAME** is default key name for
  device update agent. Default value is "example-com.key.pem".
- **PART_NAME_MTD_CERT** is mtd partition name for secure data store.
  Default value is "Secure".
- **FUS_AZURE_CONFIGURATION** is path to configuration for device update agent. Default value is "/etc/adu/du-config.json".
- **BUILD_X509_CERTIFICATE_STORE_MOUNT** enables support for certificates storage. Default value is "ON".
