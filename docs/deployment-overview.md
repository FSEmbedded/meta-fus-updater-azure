## Overview FSUP framework images

The image *fus-image-update-azure-std* extends the *fus-image-update-std* image configuration and creates additional images for the FSUP framework.

### FSUP framework update images

In addition to the update images, manifests for Azure Cloud are created.

For example, where `<dev>` is `emmc` or `nand`:

- for `application.fs`:
  `<MANIFEST_PROVIDER>.<MANIFEST_UPDATE_NAME>-common-app.<date>.importmanifest.json`
- for `firmware_<dev>.fs`:
  `<MANIFEST_PROVIDER>.<MANIFEST_UPDATE_NAME>-common-fw-<dev>.<date>.importmanifest.json`
- for `update_<dev>.fs`:
  `<MANIFEST_PROVIDER>.<MANIFEST_UPDATE_NAME>-common-update-<dev>.<date>.importmanifest.json`
