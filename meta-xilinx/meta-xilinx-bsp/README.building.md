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

Using multiconfig to build ZU+
------------------------------

multiconfig dependency has to be added in image file or local.conf.
For example in core-image-minimal you will need  
do_image[mcdepends] = "multiconfig:zcu102:pmu:pmu-firmware:do_deploy"

Add conf/multiconfig in the build directory and create pmu.conf and zcu102.conf

Add the following in pmu.conf:  
	MACHINE="zynqmp-pmu"  
	DISTRO="xilinx-standalone"  
    	GCCVERSION="7.%"  
    	TMPDIR="${TOPDIR}/pmutmp"  

Add the following in zcu102.conf:  
	MACHINE="zcu102-zynqmp"  
    	DISTRO="poky"  

In local.conf multiconfig is enabled by: BBMULTICONFIG ?= "zcu102 pmu"

bitbake multiconfig:zcu102:core-image-minimal  

More information about multiconfig:
https://www.yoctoproject.org/docs/current/mega-manual/mega-manual.html#dev-building-images-for-multiple-targets-using-multiple-configurations

Workaround:  
There is additional workaround required in u-boot-xlnx recipe. Add the below dependency  
do_compile[mcdepends] = "multiconfig:zcu102:pmu:pmu-firmware:do_deploy"

Additional Information
----------------------

For more complete details on setting up and using Yocto/OE refer to the Yocto
Project Quick Start guide available at:
	http://www.yoctoproject.org/docs/current/yocto-project-qs/yocto-project-qs.html

