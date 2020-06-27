# Layer Contents

## Supported Machines

* raspberrypi
* raspberrypi0
* raspberrypi0-wifi
* raspberrypi2
* raspberrypi3
* raspberrypi3-64 (64 bit kernel & userspace)
* raspberrypi4
* raspberrypi4-64 (64 bit kernel & userspace)
* raspberrypi-cm (dummy alias for raspberrypi)
* raspberrypi-cm3

Note: The raspberrypi3 machines include support for Raspberry Pi 3B+.

## Images

* rpi-test-image
  * Image based on core-image-base which includes most of the packages in this
    layer and some media samples.

For other uses it's recommended to base images on `core-image-minimal` or
`core-image-base` as appropriate. The old image names (`rpi-hwup-image` and
`rpi-basic-image`) are deprecated.

## WiFi and Bluetooth Firmware

Be aware that the WiFi and Bluetooth firmware for the supported boards
is not available in the base version of `linux-firmware` from OE-Core
(poky). The files are added from Raspbian repositories in this layer's
bbappends to that recipe. All machines define
`MACHINE_EXTRA_RRECOMMENDS` to include the required wireless firmware;
raspberrypi3 supports 3, 3B, and 3B+ and so include multiple firmware
packages.
