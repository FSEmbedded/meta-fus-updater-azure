## Description of the meta-fus-updater-azure layer

### classes/fsup-provisioning.bbclass

Extends the build process to create manifests for update images, create X.509 certificates and
register a set of devices with the Azure Device Provisioning Service.

The build process runs these functions as image postprocess commands:
- **create_update_manifest_images** creates manifests for Azure Cloud.
  Required to deploy update images.
- **create_device_certificate** creates X.509 certificates
  and registers devices with the Azure Device Provisioning Service.
  There are the following X.509 certificate types:
  - *root* are certificates for the Azure Device Provisioning Service,
    such as root or intermediate certificates.
  - *device* are certificates for IoT devices. They are packed into the
    certs.fs blob, which must be available on the IoT device to communicate with Azure Cloud.

### conf/layer.conf

Ensures that the build system uses the correct paths and priority to find and
process the recipes and metadata in the layer.
- compatible: kirkstone, scarthgap
- priority: 11

### includes/adu_paths.inc

Central definition of all ADU directory paths used across recipes.
Weak assignments allow distro/machine overrides.

### recipes-application/*

Extends the application configuration to overlay additional directories.

### recipes-azure-blob-storage-file-upload-utility/*

A Microsoft Azure package that exposes a C interface to upload the files passed to the utility to an Azure Blob Storage account using a SAS URL.

### recipes-azure-device-update/*

Provides the Azure device update agent with additional required packages and artifacts.
- *adu-agent-service* deploys the systemd service for device update
- *adu-device-info-files* generates additional info files for device update,
  *adu-manufacturer*, *adu-model* and *adu-version*,
  and deploys them to the */etc* directory.
- *adu-hw-compat* generates the ADU hardware compatibility info file
  and deploys it to the */etc* directory.
- *adu-log-dir* installs a tmpfiles.d rule that creates the *ADUC_LOG_DIR*
  directory for ADU log files at boot.
- *azure-device-update* builds and installs the device update agent and deploys the
  *tools/AduCmdlets* scripts to the *iot_hub_scripts* directory.
- *deliveryoptimization-agent* builds and installs the
  delivery optimization simple client.
- *deliveryoptimization-agent-service* installs and configures
  the delivery optimization agent service.
- *deliveryoptimization-sdk* builds and installs the
  delivery optimization client C++ SDK.

### recipes-azure-iot/*

- *azure-iot-sdk-c* builds and installs the azure-iot-sdk-c
  with PnP support.
- *fs-provisioning-client* builds a client to register
  IoT devices and deploys the scripts
  and configuration files for the *create_device_certificate* function.

### recipes-azure-sdk-for-cpp/*

Builds and installs the Microsoft Azure SDK for C++.

### recipes-config/*

Description of the *fus-image-update-azure-std* image.

### recipes-devtools/*

Adapts the abseil-cpp library, which is used by the OpenTelemetry package.

### recipes-dynamic_overlay/*

Configures additional CMake parameters to add the X.509 certificate
store on the secure partition, see [dynamic-overlay.md](dynamic-overlay.md).

### recipes-msft-gsl/*

Builds and installs the Microsoft Guidelines Support Library (GSL).

### recipes-opentelemetry-cpp/*

Builds and installs the OpenTelemetry C++ client library.

### wic/*

File to generate an SD card image for eMMC with Azure support.

### docs/*

Documentation in markdown format.
