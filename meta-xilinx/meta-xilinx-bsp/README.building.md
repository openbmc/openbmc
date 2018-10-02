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
		<path to layer>/meta-xilinx-contrib \
		"

meta-xilinx-contrib is a contribution layer and is optional.

To build a specific target BSP configure the associated machine in `local.conf`:

	MACHINE ?= "zc702-zynq7"

Build the target file system image using `bitbake`:

	$ bitbake core-image-minimal

Once complete the images for the target machine will be available in the output
directory `tmp/deploy/images/<machine name>/`.

Additional Information
----------------------

For more complete details on setting up and using Yocto/OE refer to the Yocto
Project Quick Start guide available at:
	http://www.yoctoproject.org/docs/current/yocto-project-qs/yocto-project-qs.html

