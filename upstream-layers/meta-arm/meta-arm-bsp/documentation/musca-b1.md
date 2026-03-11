# Musca B1

## Overview
For a description of the hardware, go to
https://developer.arm.com/tools-and-software/development-boards/iot-test-chips-and-boards/musca-b-test-chip-board

For emulated hardware, go to
https://www.qemu.org/docs/master/system/arm/musca.html

## Building
In the local.conf file, MACHINE should be set as follows:
MACHINE ?= "musca-b1"

To build the trusted firmware-m:
```bash$ bitbake trusted-firmware-m```
