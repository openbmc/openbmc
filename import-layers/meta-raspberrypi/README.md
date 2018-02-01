# meta-raspberrypi

Yocto BSP layer for the Raspberry Pi boards - <http://www.raspberrypi.org/>.

[![Build Status](https://yocto-ci.resin.io/job/meta-raspberrypi1/badge/icon)](https://yocto-ci.resin.io/job/meta-raspberrypi1)
[![Build Status](https://yocto-ci.resin.io/job/meta-raspberrypi2/badge/icon)](https://yocto-ci.resin.io/job/meta-raspberrypi2)
[![Build Status](https://yocto-ci.resin.io/job/meta-raspberrypi3/badge/icon)](https://yocto-ci.resin.io/job/meta-raspberrypi3)
[![Gitter chat](https://badges.gitter.im/gitterHQ/gitter.png)](https://gitter.im/agherzan/meta-raspberrypi)

## Quick links

* Git repository web frontend:
  <http://git.yoctoproject.org/cgit/cgit.cgi/meta-raspberrypi/>
* Mailing list (yocto mailing list): <yocto@yoctoproject.org>
* Issues management (Github Issues):
  <https://github.com/agherzan/meta-raspberrypi/issues>

## Description

This is the general hardware specific BSP overlay for the RaspberryPi device.

More information can be found at: <http://www.raspberrypi.org/> (Official Site)

The core BSP part of meta-raspberrypi should work with different
OpenEmbedded/Yocto distributions and layer stacks, such as:

* Distro-less (only with OE-Core).
* Angstrom.
* Yocto/Poky (main focus of testing).

## Dependencies

This layer depends on:

* URI: git://git.yoctoproject.org/poky
  * branch: pyro
  * revision: HEAD

* URI: git://git.openembedded.org/meta-openembedded
  * layers: meta-oe, meta-multimedia, meta-networking, meta-python
  * branch: pyro
  * revision: HEAD

## Quick Start

1. source poky/oe-init-build-env rpi-build
2. Add this layer to bblayers.conf and the dependencies above
3. Set MACHINE in local.conf to one of the supported boards
4. bitbake rpi-hwup-image
5. dd to a SD card the generated sdimg file (use xzcat if rpi-sdimg.xz is used)
6. Boot your RPI.

## Maintainers

* Andrei Gherzan `<andrei at gherzan.ro>`
