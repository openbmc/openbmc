meta-xilinx-standalone
======================

This layer is meant to augment Yocto/OE functionality to provide a Baremetal/Standalone Toolchain.


Maintainers, Mailing list, Patches
==================================

Please send any patches, pull requests, comments or questions for this layer to
the [meta-xilinx mailing list](https://lists.yoctoproject.org/listinfo/meta-xilinx):

	meta-xilinx@lists.yoctoproject.org

Maintainers:

	Alejandro Enedino Hernandez Samaniego <alejandr@xilinx.com>
	Manjukumar Harthikote Matha <manjukumar.harthikote-matha@xilinx.com>

Dependencies
============

This layer depends on:

     URI: git://git.yoctoproject.org/poky

     URI: git://git.yoctoproject.org/meta-xilinx/meta-xilinx-bsp

Usage
=====

1.- Clone this layer along with the specified layers

2.- $ source oe-init-build-env

3.- Add this layer to BBLAYERS on conf/bblayers.conf

4.- Add the following to your conf/local.conf to build for the microblaze architecture:

DISTRO="xilinx-standalone"

MACHINE="zynqmp-pmu"

GCCVERSION="7.%"

5.- Build a package:

for example:

$ bitbake newlib

or

$ bitbake meta-toolchain
