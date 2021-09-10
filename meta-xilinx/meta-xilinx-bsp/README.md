meta-xilinx
===========

This layer provides support for MicroBlaze, Zynq and ZynqMP.

Additional documentation:

* [Building](README.building.md)
* [Booting](README.booting.md)

Supported Boards/Machines
=========================

Boards/Machines supported by this layer:

* MicroBlaze:
  * [Xilinx ML605 (QEMU)](conf/machine/ml605-qemu-microblazeel.conf) - `ml605-qemu-microblazeel` (QEMU support)
  * [Xilinx S3A DSP 1800 (QEMU)](conf/machine/s3adsp1800-qemu-microblazeeb.conf) - `s3adsp1800-qemu-microblazeeb` (QEMU support)
  * [Xilinx KC705](conf/machine/kc705-microblazeel.conf) - `kc705-microblazeel`
* Zynq:
  * [Zynq (QEMU)](conf/machine/qemu-zynq7.conf) - `qemu-zynq7` (QEMU Support)
  * [Xilinx ZC702](conf/machine/zc702-zynq7.conf) - `zc702-zynq7` (with QEMU support)
  * [Xilinx ZC706](conf/machine/zc706-zynq7.conf) - `zc706-zynq7` (with QEMU support)
  * [Avnet MicroZed](conf/machine/microzed-zynq7.conf) - `microzed-zynq7`
  * [Avnet PicoZed](conf/machine/picozed-zynq7.conf) - `picozed-zynq7`
  * [Avnet/Digilent ZedBoard](conf/machine/zedboard-zynq7.conf) - `zedboard-zynq7`
  * [Digilent Zybo](conf/machine/zybo-zynq7.conf) - `zybo-zynq7`
  * [Digilent Zybo Linux BD](conf/machine/zybo-linux-bd-zynq7.conf) - `zybo-linux-bd-zynq7`
* ZynqMP:
  * [Xilinx ZCU102](conf/machine/zcu102-zynqmp.conf) - `zcu102-zynqmp` (QEMU support)
  * [Xilinx ZCU106](conf/machine/zcu106-zynqmp.conf) - `zcu106-zynqmp`
  * [Xilinx ZCU104](conf/machine/zcu104-zynqmp.conf) - `zcu104-zynqmp`

Additional information on Xilinx architectures can be found at:
	http://www.xilinx.com/support/index.htm

For Zybo Linux BD reference design, please see meta-xilinx-contrib layer

Maintainers, Mailing list, Patches
==================================

Please send any patches, pull requests, comments or questions for this layer to
the [meta-xilinx mailing list](https://lists.yoctoproject.org/listinfo/meta-xilinx):

	meta-xilinx@lists.yoctoproject.org

Maintainers:

	Sai Hari Chandana Kalluri <chandana.kalluri@xilinx.com>
	Mark Hatle <mark.hatle@xilinx.com>

Dependencies
============

This layer depends on:

	URI: git://git.openembedded.org/bitbake

	URI: git://git.openembedded.org/openembedded-core
	layers: meta

Recipe Licenses
===============

Due to licensing restrictions some recipes in this layer rely on closed source
or restricted content provided by Xilinx. In order to use these recipes you must
accept or agree to the licensing terms (e.g. EULA, Export Compliance, NDA,
Redistribution, etc). This layer **does not enforce** any legal requirement, it
is the **responsibility of the user** the ensure that they are in compliance
with any licenses or legal requirements for content used.

In order to use recipes that rely on restricted content the `xilinx` license
flag must be white-listed in the build configuration (e.g. `local.conf`). This
can be done on a per package basis:

	LICENSE_FLAGS_WHITELIST += "xilinx_pmu-rom"

or generally:

	LICENSE_FLAGS_WHITELIST += "xilinx"

Generally speaking Xilinx content that is provided as a restricted download
cannot be obtained without a Xilinx account, in order to use this content you
must first download it with your Xilinx account and place the downloaded content
in the `downloads/` directory of your build or on a `PREMIRROR`. Attempting to
fetch the content using bitbake will fail, indicating the URL from which to
acquire the content.

