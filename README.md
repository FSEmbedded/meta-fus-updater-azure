# Introduction

The layer **meta-fus-updater-azure** is a extension for **meta-fus-updater** layer
and adds over the air update (OTA) from Microsoft Azure Cloud.

## Overview - Suppored architecture

See readme from **meta-fus-updater** layer.

## Building images

See readme from **meta-fus-updater** layer.

The layer provides additional image:

| Image name     | Description             |
|----------------|-------------------------|
| fus-image-update-azure-std  | Standard image with fsup framework with azure integration (Weston) |

## Table of contents

- [Layer Overview](docs/layer-description.md)
- Core Components of Azure Integration
    - [Azure IOT SDK C](docs/azure-iot-sdk-c.md)
    - [Azure Device Update](docs/azure-device-update.md)
    - [Dynamic Overlay Extension](docs/dynamic-overlay.md)
- [Structure of Deploy Directory](docs/deployment-overview.md)
