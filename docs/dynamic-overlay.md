## Extended configuration for dynamic overlay

The *recipes-dynamic_overlay* directory extends the recipe from *meta-fus-updater* with the X.509 certificate store for Azure Device Update.

### Enabling the certificate store

The certificate store is enabled by `ENABLE_X509_CERT`, which defaults to "1".
It adds the PACKAGECONFIG option *use-x509-cert*, which builds dynamic overlay with
`BUILD_X509_CERTIFICATE_STORE_MOUNT=ON` (the CMake default is "OFF") and adds
*jsoncpp* and *libarchive* as dependencies.

### Additional definitions

With the certificate store enabled, all of the following definitions are required.
They have no CMake default; the build fails if one of them is empty.
The layer sets them as follows:

- **TARGET_ARCHIV_DIR_PATH** is the path to the X.509 certificates.
  Set to `${ADUC_X509_DIR}`, default "/etc/adu/x509_c".
- **FUS_AZURE_CONFIGURATION** is the path to the configuration of the device update agent.
  Set to `${ADUC_DU_CONFIG}`, default "/etc/adu/du-config.json".
- **FUS_AZURE_DOWNLOADS_DIR** is the downloads folder of the device update agent.
  Set to `${ADUC_DOWNLOADS_DIR}`, default "/tmp/adu/downloads".
- **PART_NAME_MTD_CERT** is the MTD partition name of the secure data store on NAND.
  Set to "Secure".
- **EMMC_SECURE_PART_BLK_NR** is the start sector (512 bytes) of the secure data store on eMMC.
  Set to "16384", the 8 MiB offset of the *secure* partition in the wks file.

The `ADUC_*` defaults come from *includes/adu_paths.inc*.

### Configurations from older provisioning tools

Dynamic overlay only accepts a *du-config.json* whose *x509_container* is the
configured **TARGET_ARCHIV_DIR_PATH**. As the only exception, the legacy value
"/adu/x509_c" written by older provisioning tools is rewritten to
**TARGET_ARCHIV_DIR_PATH**, and its *downloadsFolder* to **FUS_AZURE_DOWNLOADS_DIR**.
Any other *x509_container* path is rejected.
