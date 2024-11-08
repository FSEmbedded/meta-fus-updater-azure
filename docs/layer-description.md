## Description of the meta-fus-updater-azure layer

### classes/fsup-provisioning.bbclass:

Extends build process to create manifests for update images, creates X509 certificates and
register set of devices on azure provisioning service.

The build process starts the functions in image post process
- **create_update_manifest_images** task creates manifests for Azure Cloud.
  Required to deploy update images.
- **create_device_certificate** task creates X509 certificates
  and register devices on azure provisioning service
  They are following X509 certificates types
  - *root* are certificates for the azure provisioning service
    like root or intermediate.
  - *device* are certificates for iot devices. E.g. blob certs.fs
    must be available on the iot device to communicate with Azure Cloud.

### conf/layer.conf:

Ensures that the build system uses correct paths and priority to find and
process the recipes and metadata in the layer.
- compatible: kirkstone
- priority: 11


### recipes-application/*:

Extends application configuration to overlay additional directories.

### recipes-azure-blob-storage-file-upload-utility/*:

Microsoft Azure package provides the functions necessary to upload files passed to the utility to an Azure Blob Storage account using a SAS url by exposing a C interface.

### recipes-azure-device-update/*:

Provides azure device update agent with additional required packages and artifacts.
- *adu-agent-service* deploys systemd service for device update
- *adu-device-info-files* generates additional info files for device update
   *adu-manufacturer*, *adu-model* and *adu-version*
   and deploy it to */etc/ directory.
- *adu-hw-compat* generates ADU hardware compatibility info file
  and deploy it to */etc* directory
- *adu-log-dir* generates new */tmp/adu* directory for ADU log files
- *adu-pub-key* generates and copies/installs the public key
  .pem file used to validate the signatures of images.
- *azure-device-update* build and install device update agent and
   *tools/Aducmdlets* scripts to *iot_hub_scripts* directory.
- *deliveryoptimization-agent* build and install
  delivery optimization simple client.
- *deliveryoptimization-agent-service* installs and configures
  the delivery optimization agent service
- *deliveryoptimization-agent-sdk* build and install the
  delivery optimization client CPP SDK.

### recipes-azure-iot/*:

- *azure-iot-sdk-c* build and install the azure-iot-sdk-c
  with PnP support.
- *fs-provisioning-client* creates a client to register
  iot devices and deploy scripts
  and configuration files for *create_device_certificate* task.

### recipes-azure-sdk-for-cpp/*:

Build and install the azure-blob-storage-file-upload-utility

### recipes-config/*:

Description of *fus-image-update-azure-std* image

### recipes-devtools/*:

Build addtional library abseil-cpp. Used by telemetry package.

### recipes-dynamic_overlay/*:

Confugures additional parameter for cmake to add secure
partition support.

### recipes-msft-gsl/*:

Build telemetry integration.

### recipes-opentelemetry-cpp/*:

Build and install the Microsoft GSL library

### wic/*:

File to generate sdcard image for eMMC with Azure support.

### docs/*:

Documentation in markdown format.