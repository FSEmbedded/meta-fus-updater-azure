# Microsoft Azure IoT SDKs and libraries for C

The Azure IOT Hub Device SDK allows applications written in C99 or later or C++ to communicate easily with Azure IoT Hub, Azure IoT Central and to Azure IoT Device Provisioning.


### Integration of FSUP Framework

FSUP Framework extends provisiong client to use custom X509 certifactes. For this device certificates would be created. The *fus_prov_dps_client* use X509 certifcates to connect to Azure Provisiong service and to register
devices for a given group.

The framework offers a set of tools, scripts and configuration files to create cerficates and configuration files. All files and script can be found in *<build dir>/tmp/deploy/images/<architecture>/fs-provisioning* directory.

### Directory overview fs-provisioning

E.g.

- [root] directory
  - *addfsheader* - help script to create fus header
  - *fus_prov_dps_client* - fus provisiong client
  - *provisioning* - main script to create certificates and
    register device by Azure Device Provisioning Service (DPS).
  - *template-du-config.json* - template for device configuration.
    Is used by provisiong script to create certs.fs binary for
    device "Secure" partition

- [x509] directory
    - is a copy of "[azure-iot-sdk-c]/tools/AduCmdlets" directory.
      Use *certGen* script and *openssl_root_ca.cnf*, *openssl_device_intermediate_ca.cnf* to generate openssl certificates
- [name of architecure] directory
    - <certs> folder with root and interediate ca
    - <devices> folder with device ca
    - ...

# Device binaries

For every listed device in *UPDATE_DEVICES_LIST* env. the function
**create_device_certificate** creates certificats and confiugration file
du-config.json. Both are part of certs.fs

Configuration file is used by device update agent to get informations about iot hub, certificates type and ssh keys.

**template-du-config.json**

```json
{
    "schemaVersion": "1.1",
    "aduShellTrustedUsers": [
      "adu",
      "do"
    ],
    "manufacturer": <manufacturer>,
    "model": <model>,
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
E.g. configuration file for device name *dev01*
```json
{
    "schemaVersion": "1.1",
    "aduShellTrustedUsers": [
      "adu",
      "do"
    ],
    "manufacturer": "FUS",
    "model": "fsimx8mp",
    "agents": [
      {
        "name": "fus/update",
        "runas": "adu",
        "connectionSource": {
          "x509_container": "/adu/x509_c",
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


### Environments

For the build process following environmets can be adapted:

- *FS_PROVISIONING_SERVICE_DIR_NAME* directory name of provisioning service
- *FS_PROVISIONING_UPDATE_DEVICES_SUBDIR* directory name for iot devices
- *UPDATE_DEVICES_LIST* list of device for *provisioning* script
- *FS_PROVISIONING_IOTHUB* prefix of IOT Hub URL for configuration
- *FS_PROVISIONING_DPS_IDSCOPE* id scope of device provisiong service for configuration
- *APPLICATION_VERSION* application version.
- *APPLICATION_CONTAINER_NAME* name of application container
- *FIRMWARE_VERSION* firmware version
- *MANIFEST_PROVIDER* manifest provider for Azure manifest
- *MANIFEST_UPDATE_NAME* update name for Azure manifest
- *MANIFEST_FW_UPDATE_VERSION firmware update version for Azure manifest
- *MANIFEST_APP_UPDATE_VERSION* application update version for Azure manifest
- *MANIFEST_DEVICE_MOD* device modell for azure Manifest

