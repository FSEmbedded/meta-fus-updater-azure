## FSUP Framework Device Update

The core component of FSUP framework is extended package *iot-hub-device-update*. The package
creates a device update client which allows connection to Azure IOT Hub to handle OTA functionality.

The device update tool is running in background as deamon. The core tasks are
- connect to IOT Hub
- receive property updates when device twin changes
- evaluate "update actions"
- delegate processing of the metadata to a set of extension plugins
- reports state changes and result codes by patching the twins

Device update agent can support multiple handler types at the same type. A step handler is an
extension for specific update type.

### fsupdate step handler

FSUPDATE step hanlder *fsupdate_handler* extends device update agent to handle FSUP framework
update types. It implements required functions to download, install, apply, check for update functions
and use FUS CLI to get specific state or start specific process like image installation.

### fsupdate adu shell tasks

To use other tools like *apt* adu shell is implemented. The *adu-shell* tool is part of the package and
has special permissions to create sub processes.

For the framework fsupdate tasks are added to call *fs-updater* with required argument. This constuct allows
device update agent to call *fs-updater* corresponding to own workflow function without changing of permissions.

# Help scirpts for Azure manifests

The build process uses scripts from *tools/AduCmdlets* directory to create manifests. The scripts would be installed in deploy process and can be found in *<build dir>/tmp/deploy/images/<architecture>/iot_hub_scripts* directory.
