# Introduction

The layer **meta-fus-updater-azure** is an extension of the **meta-fus-updater** layer
and adds over-the-air (OTA) updates from Microsoft Azure Cloud.

## Overview - Supported architecture

See the README of the **meta-fus-updater** layer.

## Building images

See the README of the **meta-fus-updater** layer.

The layer provides an additional image:

| Image name     | Description             |
|----------------|-------------------------|
| fus-image-update-azure-std  | Standard image with FSUP framework and Azure integration (Weston) |

## Table of contents

- [Layer Overview](docs/layer-description.md)
- Core Components of Azure Integration
    - [Azure IoT SDK C](docs/azure-iot-sdk-c.md)
    - [Azure Device Update](docs/azure-device-update.md)
    - [Dynamic Overlay Extension](docs/dynamic-overlay.md)
- [Structure of Deploy Directory](docs/deployment-overview.md)
