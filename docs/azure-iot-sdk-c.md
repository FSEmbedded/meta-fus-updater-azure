# Microsoft Azure IoT SDKs and libraries for C

The Azure IoT Hub Device SDK allows applications written in C99 or later, or in C++, to communicate easily with Azure IoT Hub, Azure IoT Central and Azure IoT Device Provisioning.

## Integration of FSUP Framework

The FSUP framework extends the provisioning client to use custom X.509 certificates and creates a device certificate for every device. The *fus_prov_dps_client* uses X.509 certificates to connect to the Azure Device Provisioning Service and to register
devices for a given group.

The framework offers a set of tools, scripts and configuration files to create certificates and device configurations. All files and scripts can be found in the *&lt;build dir&gt;/tmp/deploy/images/&lt;machine&gt;/fs-provisioning* directory.

## Directory overview of fs-provisioning

For example:

- [root] directory
  - *fus_prov_dps_client* - provisioning client
  - *provisioning.sh* - main script to create certificates and
    register devices with the Azure Device Provisioning Service (DPS).
    It calls *addfsheader.sh*, which *meta-fus-updater* provides with the
    recipe *fus-installscript-native*; it must be in the PATH
  - *template-du-config.json* - template for the device configuration.
    Used by the provisioning script to create the certs.fs binary for
    the device's "Secure" partition
- [x509] directory
  - a copy of the "[azure-iot-sdk-c]/tools/CACertificates" directory.
    Use the *certGen.sh* script with *openssl_root_ca.cnf* and *openssl_device_intermediate_ca.cnf* to generate the certificates
- [FS_PROVISIONING_UPDATE_DEVICES_SUBDIR] directory, default *fus-devices*
  - *certs* folder with the root and intermediate certificates
  - *private* folder with the private keys
  - *devices* folder with one directory per device ID, holding *x509_c/* with the
    device certificate and key, *du-config.json*, *certs.tar.bz2* and *certs.fs*
    (the image for the device's "Secure" partition)
  - ...

## Device binaries

If *FS_PROVISIONING_DPS_IDSCOPE* and *FS_PROVISIONING_IOTHUB* are set, the function
**create_device_certificate** creates, for every device listed in *UPDATE_DEVICES_LIST*,
the certificates and the configuration file du-config.json. Both are part of certs.fs.
Otherwise the build only prints a warning.

The device update agent reads the configuration file to get the IoT hub, the certificate type and the keys.

**template-du-config.json**

```text
{
    "schemaVersion": "1.1",
    "aduShellTrustedUsers": [
      "adu",
      "do"
    ],
    "manufacturer": <manufacturer>,
    "model": <model>,
    "downloadsFolder": <downloads_folder>,
    "agents": [
      {
        "name": <name>,
        "runas": "adu",
        "connectionSource": {
          "x509_container": <x509_store>,
          "x509_cert": <x509_cert>,
          "x509_key": <x509_key>,
          "device_id": <device_id>,
          "iotHubName": <iothub_name>,
          "iotHubSuffix": <iothub_suffix>,
          "connectionType": <connection_type>,
          "connectionData": <connection_data>
        },
        "manufacturer": <manufacturer>,
        "model": <model>
      }
    ]
}
```
Example configuration file for the device *dev01*:
```json
{
    "schemaVersion": "1.1",
    "aduShellTrustedUsers": [
      "adu",
      "do"
    ],
    "manufacturer": "FUS",
    "model": "fsimx8mp",
    "downloadsFolder": "/tmp/adu/downloads",
    "agents": [
      {
        "name": "fus/update",
        "runas": "adu",
        "connectionSource": {
          "x509_container": "/etc/adu/x509_c",
          "x509_cert": "dev01.cert.pem",
          "x509_key": "dev01.key.pem",
          "device_id": "dev01",
          "iotHubName": "fusiothub",
          "iotHubSuffix": "azure-devices.net",
          "connectionType": "x509",
          "connectionData": ""
        },
        "manufacturer": "FUS",
        "model": "fsimx8mp"
      }
    ]
}
```

## Environment variables

The following variables can be adapted for the build process:

- *FS_PROVISIONING_SERVICE_DIR_NAME* directory name of the provisioning service
- *FS_PROVISIONING_UPDATE_DEVICES_SUBDIR* directory name for IoT devices
- *UPDATE_DEVICES_LIST* list of devices for the *provisioning.sh* script
- *FS_PROVISIONING_IOTHUB* prefix of the IoT Hub URL for the configuration
- *FS_PROVISIONING_DPS_IDSCOPE* ID scope of the device provisioning service for the configuration
- *APPLICATION_VERSION* application version
- *APPLICATION_CONTAINER_NAME* name of the application container
- *FIRMWARE_VERSION* firmware version
- *MANIFEST_PROVIDER* manifest provider for the Azure manifest, also used as
  *manufacturer* in *du-config.json*
- *MANIFEST_UPDATE_NAME* update name for the Azure manifest
- *MANIFEST_FW_UPDATE_VERSION* firmware update version for the Azure manifest
- *MANIFEST_APP_UPDATE_VERSION* application update version for the Azure manifest
- *MANIFEST_DEVICE_MOD* device model for the Azure manifest, also used as
  *model* in *du-config.json*
- *ADUC_DOWNLOADS_DIR* download directory for the update agent, default "/tmp/adu/downloads".
  Dynamic overlay is built with the same value, see [dynamic-overlay.md](dynamic-overlay.md).
