## Overview FSUP framework images

The image *fus-image-updater-azure-std* extends *fus-image-updater-std* image configuration and creates additional images for FSUP framework.

### FSUP framework update images

Additional to the update images manifests for Azure Cloud would be created

E.g.
- for application_[bootdevice].fs
  <MANIFEST_PROVIDER>.<MANIFEST_UPDATE_NAME>-common-app.<date>.importmanifest.json
- for firmware_[bootdevice].fs
  <MANIFEST_PROVIDER>.<MANIFEST_UPDATE_NAME>-common-fw-[nand/emmc].<date>.importmanifest.json
- for update_[bootdevice].fs
  <MANIFEST_PROVIDER>.<MANIFEST_UPDATE_NAME>-common-update-[nand/emmc].<date>.importmanifest.json
