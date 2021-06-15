# meta-raspberrypi

Yocto BSP layer for the Raspberry Pi boards - <http://www.raspberrypi.org/>.

[![Build Status](https://yocto-ci.resin.io/job/meta-raspberrypi1/badge/icon)](https://yocto-ci.resin.io/job/meta-raspberrypi1)
[![Build Status](https://yocto-ci.resin.io/job/meta-raspberrypi2/badge/icon)](https://yocto-ci.resin.io/job/meta-raspberrypi2)
[![Build Status](https://yocto-ci.resin.io/job/meta-raspberrypi3/badge/icon)](https://yocto-ci.resin.io/job/meta-raspberrypi3)
[![Build Status](https://yocto-ci.resin.io/job/meta-raspberrypi4/badge/icon)](https://yocto-ci.resin.io/job/meta-raspberrypi4)
[![Documentation Status](https://readthedocs.org/projects/meta-raspberrypi/badge/?version=latest)](https://meta-raspberrypi.readthedocs.io/en/latest/?badge=latest)
[![Matrix](https://img.shields.io/matrix/meta-raspberrypi:cub.icu.svg?server_fqdn=matrix.cub.icu)](https://matrix.to/#/#meta-raspberrypi:cub.icu)

|                           |                                                           |
|:-:                        | :-:                                                       |
| Build server sponsored by | [![balena.io](./img/balena.png)](https://www.balena.io/). |

## Quick links

* Git repository web frontend:
  <https://github.com/agherzan/meta-raspberrypi>
* Mailing list (yocto mailing list): <yocto@yoctoproject.org>
* Issues management (Github Issues):
  <https://github.com/agherzan/meta-raspberrypi/issues>
* Documentation: <http://meta-raspberrypi.readthedocs.io/en/latest/>

## Description

This is the general hardware specific BSP overlay for the RaspberryPi device.

More information can be found at: <http://www.raspberrypi.org/> (Official Site)

The core BSP part of meta-raspberrypi should work with different
OpenEmbedded/Yocto distributions and layer stacks, such as:

* Distro-less (only with OE-Core).
* Yoe Disto (Video and Camera Products).
* Yocto/Poky (main focus of testing).

## Dependencies

This layer depends on:

* URI: git://git.yoctoproject.org/poky
  * branch: master
  * revision: HEAD

* URI: git://git.openembedded.org/meta-openembedded
  * layers: meta-oe, meta-multimedia, meta-networking, meta-python
  * branch: master
  * revision: HEAD

## Quick Start

1. source poky/oe-init-build-env rpi-build
2. Add this layer to bblayers.conf and the dependencies above
3. Set MACHINE in local.conf to one of the supported boards
4. bitbake core-image-base
5. Use bmaptool to copy the generated .wic.bz2 file to the SD card
6. Boot your RPI

## Quick Start with kas

1. Install kas build tool from PyPi (sudo pip3 install kas)
2. kas build meta-raspberrypi/kas-poky-rpi.yml
3. Use bmaptool to copy the generated .wic.bz2 file to the SD card
4. Boot your RPI

To adjust the build configuration with specific options (I2C, SPI, ...), simply add
a section as follows:

```
local_conf_header:
  rpi-specific: |
    ENABLE_I2C = "1"
    RPI_EXTRA_CONFIG = "dtoverlay=pi3-disable-bt"
```

To configure the machine, you have to update the `machine` variable.
And the same for the `distro`.

For further information, you can read more at <https://kas.readthedocs.io/en/1.0/index.html>

## Maintainers

* Andrei Gherzan `<andrei at gherzan.ro>`
