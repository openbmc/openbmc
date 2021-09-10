meta-xilinx-standalone
======================

This layer is meant to augment Yocto/OE functionality to provide a 
Baremetal/Standalone Toolchain as well as the foundation for building
the embeddedsw components that enable non-Linux software required for
Xilinx based FPGA/SOCs.

Note, the non-Linux software components are still in development and
this should be considered to be a preview release only.  For instance,
some components may not be buildable, expect APIs to change on various
parts and pieces.


Maintainers, Mailing list, Patches
==================================

Please send any patches, pull requests, comments or questions for this 
layer to the [meta-xilinx mailing list]
(https://lists.yoctoproject.org/listinfo/meta-xilinx):

	meta-xilinx@lists.yoctoproject.org

Maintainers:

	Sai Hari Chandana Kalluri <chandana.kalluri@xilinx.com>
	Mark Hatle <mark.hatle@xilinx.com>

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

4.- Add the following to your conf/local.conf to build for the 
microblaze architecture:

DISTRO="xilinx-standalone"

MACHINE="microblaze-pmu"

5.- Build a package:

for example:

$ bitbake newlib

or

$ bitbake meta-toolchain
