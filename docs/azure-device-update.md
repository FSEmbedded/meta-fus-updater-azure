## FSUP Framework Device Update

The core component of the Azure integration of the FSUP framework is the recipe *azure-device-update*, an extended Azure *iot-hub-device-update*
fetched from *fus-device-update-azure*. The package
creates a device update client which connects to Azure IoT Hub to handle OTA updates.

The device update agent runs in the background as a daemon. Its core tasks are:
- connect to IoT Hub
- receive property updates when the device twin changes
- evaluate "update actions"
- delegate processing of the metadata to a set of extension plugins
- report state changes and result codes by patching the device twin

The device update agent can support multiple handler types at the same time. A step handler is an
extension for a specific update type.

### fsupdate step handler

The step handler *fsupdate_handler* extends the device update agent to handle FSUP framework
update types. It implements the functions required to download, install, apply and check for updates,
and uses the *fs-updater* CLI to query the update state or to start a process such as an image installation.

### fsupdate adu shell tasks

The *adu-shell* tool runs other tools such as *apt*. It is part of the package and
has special permissions to create subprocesses.

For the framework, fsupdate tasks are added that call *fs-updater* with the required arguments. This allows the
device update agent to call *fs-updater* from its own workflow functions without changing permissions.

## Helper scripts for Azure manifests

The build process uses the scripts from the *tools/AduCmdlets* directory to create manifests. The scripts are deployed to the *&lt;build dir&gt;/tmp/deploy/images/&lt;machine&gt;/iot_hub_scripts* directory.
