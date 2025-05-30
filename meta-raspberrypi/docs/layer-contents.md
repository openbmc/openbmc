# Layer Contents

## Supported Machines

* raspberrypi
* raspberrypi0
* raspberrypi0-wifi
* raspberrypi0-2w-64
* raspberrypi2
* raspberrypi3
* raspberrypi3-64 (64 bit kernel & userspace)
* raspberrypi4
* raspberrypi4-64 (64 bit kernel & userspace)
* raspberrypi5
* raspberrypi-cm (dummy alias for raspberrypi)
* raspberrypi-cm3

Note: The raspberrypi3 machines include support for Raspberry Pi 3B+.

## Multi-board Machines

This layer generally provides support for machines that are targetting a single
Raspberry Pi board (or a very few subsets of them). This is so that the build
infrastructure can tune and tweak the configuration with the flexibility to
optimise for both runtime performance and disk storage.

For usecases where compatibility of more boards is required, the layer provides
machines that are tagetting a wider support of Raspberry Pi boards.

### raspberrypi-armv7

This machine targets support for all the ARMv7-based Raspberry Pi boards. It
will pull in the firmware and deploy the kernel image and kernel modules for
all the relevant boards.

### raspberrypi-armv8

This machine targets support for all the ARMv8-based Raspberry Pi boards. It
will pull in the firmware and deploy the kernel image and kernel modules for
all the relevant boards.

## Images

* rpi-test-image
  * Image based on core-image-base which includes most of the packages in this
    layer and some media samples.

For other uses it's recommended to base images on `core-image-minimal` or
`core-image-base` as appropriate.

## WiFi and Bluetooth Firmware

Be aware that the WiFi and Bluetooth firmware for the supported boards
is not available in the base version of `linux-firmware` from OE-Core
(poky). The files are added from Raspbian repositories in this layer's
bbappends to that recipe. All machines define
`MACHINE_EXTRA_RRECOMMENDS` to include the required wireless firmware;
raspberrypi3 supports 3, 3B, and 3B+ and so include multiple firmware
packages.
