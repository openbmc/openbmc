Build Instructions
==================

The following instructions require OE-Core meta and BitBake. Poky provides these
components, however they can be acquired separately.

Initialize a build using the `oe-init-build-env` script. Once initialized
configure `bblayers.conf` by adding the `meta-xilinx-bsp` and
`meta-xilinx-contrib` layer. e.g.:

	BBLAYERS ?= " \
		<path to layer>/oe-core/meta \
		<path to layer>/meta-xilinx-bsp \
		<path to layer>/meta-xilinx-standalone \
		<path to layer>/meta-xilinx-contrib \
		"

meta-xilinx-standalone layer provides recipes which enable building baremetal
toolchain for PMU firmware. This layer is required for ZU+ devices which
depends on PMU firmware

meta-xilinx-contrib is a contribution layer and is optional.

To build a specific target BSP configure the associated machine in `local.conf`:

	MACHINE ?= "zc702-zynq7"

Build the target file system image using `bitbake`:

	$ bitbake core-image-minimal

Once complete the images for the target machine will be available in the output
directory `tmp/deploy/images/<machine name>/`.

Using SPL flow to build ZU+
------------------------------

The pmufw needs a "configuration object" to know what it should do, and it
expects to receive it at runtime.

With the U-Boot SPL workflow there's no FSBL, and passing a cfg obj to pmufw is
just not implemented in U-Boot

To work around this problem a small patch has been developed so that
pm_cfg_obj.c is linked into pmufw and loaded directly, without waiting for it
from the outside. Find the original patch on the meta-topic layer [1] and the
patch updated for pmufw 2018.x here [2].

[1]
https://github.com/topic-embedded-products/meta-topic/blob/master/recipes-bsp/pmu-firmware/pmu-firmware_2017.%25.bbappend

[2]
https://github.com/lucaceresoli/zynqmp-pmufw-builder/blob/master/0001-Load-XPm_ConfigObject-at-boot.patch


Using multiconfig to build ZU+
------------------------------

In your local.conf multiconfig should be enabled by:

`BBMULTICONFIG ?= "pmu"`

Add a directory conf/multiconfig in the build directory and create pmu.conf inside it.

Add the following in pmu.conf: 

	MACHINE="zynqmp-pmu" 
	DISTRO="xilinx-standalone" 
	TMPDIR="${TOPDIR}/pmutmp" 

Add the following in your local.conf

	MACHINE="zcu102-zynqmp" 
	DISTRO="poky" 

A multiconfig dependency has to be added in the image recipe or local.conf.

For example in core-image-minimal you would need: 

	do_image[mcdepends] = "multiconfig::pmu:pmu-firmware:do_deploy"

This creates a multiconfig dependency between the task do_image from the default multiconfig '' (which has no name)
to the task do_deploy() from the package pmu-firmware from the pmu multiconfig which was just created above.

	$ bitbake core-image-minimal

This will build both core-image-minimal and pmu-firmware.


More information about multiconfig:
https://www.yoctoproject.org/docs/current/mega-manual/mega-manual.html#dev-building-images-for-multiple-targets-using-multiple-configurations


Additional Information
----------------------

For more complete details on setting up and using Yocto/OE refer to the Yocto
Project Quick Start guide available at:
	http://www.yoctoproject.org/docs/current/yocto-project-qs/yocto-project-qs.html

