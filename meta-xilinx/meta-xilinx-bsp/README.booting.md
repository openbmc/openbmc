Booting meta-xilinx boards
==========================

Contents
--------

* [Loading via JTAG](#loading-via-jtag)
  * [XSDB](#xsdb)
  * [Load Bitstream](#load-bitstream)
  * [Load U-Boot (MicroBlaze)](#load-u-boot-microblaze)
  * [Load U-Boot (Zynq)](#load-u-boot-zynq)
  * [U-Boot Console](#u-boot-console)
  * [Kernel, Root Filesystem and Device Tree](#kernel-root-filesystem-and-device-tree)
  * [Booting via U-Boot](#booting-via-u-boot)
* [Loading via SD](#loading-via-sd)
  * [Preparing SD/MMC](#preparing-sdmmc)
  * [Installing U-Boot](#installing-u-boot)
  * [Installing Kernel and Device Tree](#installing-kernel-and-device-tree)
  * [Installing Root Filesystem](#installing-root-filesystem)
  * [U-Boot Configuration File](#u-boot-configuration-file)
  * [Booting](#booting)
* [Loading via TFTP](#loading-via-tftp)
  * [Kernel, Root Filesystem and Device Tree](#kernel-root-filesystem-and-device-tree-1)
  * [Booting via U-Boot](#booting-via-u-boot-1)


Loading via JTAG
----------------
This boot flow requires the use of the Xilinx tools, specifically XSDB and the
associated JTAG device drivers. This also requires access to the JTAG interface
on the board, a number of Xilinx and third-party boards come with on-board JTAG
modules.

### XSDB
Start `xsdb` and connect. Ensure that the target chip is visible.

	$ xsdb
	xsdb% connect
	xsdb% targets

### Load Bitstream
**(Note: This step is only required for platforms which have a bitstream e.g.
MicroBlaze.)**

Download the bitstream for the system using XSDB with the `fpga -f` command. If
a bitstream is available from meta-xilinx is will be located in the
`deploy/images/<machine-name>/` directory.

	xsdb% fpga -f download.bit

### Load U-Boot (MicroBlaze)
Download `u-boot.elf` to the target CPU via the use of XSDB.

	xsdb% targets -set -filter {name =~ "MicroBlaze*"}
	xsdb% rst
	xsdb% dow u-boot.elf
	xsdb% con

### Load U-Boot (Zynq)
Ensure the board is configured to boot from JTAG. The Zynq platform requires the
loading of SPL first, this can be done by loading the `u-boot-spl.bin` and
executing it at location `0x0`. `u-boot-spl.bin` is not output to the deploy
directory by default, it can be obtained from the work directory for U-Boot
(`git/spl/u-boot-spl.bin`) or can be extracted from `boot.bin` using
`dd if=boot.bin of=u-boot-spl.bin bs=1 skip=2240`.

	xsdb% targets -set -filter {name =~ "ARM*#0"}
	xsdb% dow -data u-boot-spl.bin 0x0
	xsdb% rwr pc 0x0
	xsdb% con

On the UART console the following should appear, indicating SPL was loaded.

	U-Boot SPL 2016.01
	Trying to boot from unknown boot device
	SPL: Unsupported Boot Device!
	SPL: failed to boot from all boot devices
	### ERROR ### Please RESET the board ###

Once SPL has loaded U-Boot can now be loaded into memory and executed. Download
`u-boot.elf` to the target.

	xsdb% stop
	xsdb% dow u-boot.elf
	xsdb% con

### U-Boot Console
U-Boot will load and the console will be available on the UART interface.

	...
	Hit any key to stop autoboot: 0
	U-Boot>

### Kernel, Root Filesystem and Device Tree
Whilst it is possible to load the images via JTAG this connection is slow and
this process can take a long time to execute (more than 10 minutes). If your
system has ethernet it is recommended that you use TFTP to load these images
using U-Boot.

Once U-Boot has been loaded, pause the execution using XSDB and use the `dow`
command to load the images into the targets memory. Once the images are loaded
continue the execution and return to the U-Boot console.

MicroBlaze (kc705-microblazeel):

	xsdb% stop
	xsdb% dow -data linux.bin.ub 0x85000000
	xsdb% dow -data core-image-minimal-kc705-microblazeel.cpio.gz.u-boot 0x86000000
	xsdb% dow -data kc705-microblazeel.dtb 0x84000000
	xsdb% con

Zynq:

	xsdb% stop
	xsdb% dow -data uImage 0x2000000
	xsdb% dow -data core-image-minimal-<machine name>.cpio.gz.u-boot 0x3000000
	xsdb% dow -data <machine name>.dtb 0x2A00000
	xsdb% con

### Booting via U-Boot
At the U-Boot console use the `bootm` command to execute the kernel.

MicroBlaze (kc705-microblazeel):

	U-Boot> bootm 0x85000000 0x86000000 0x84000000

Zynq:

	U-Boot> bootm 0x2000000 0x3000000 0x2A00000


Loading via SD
---------------------
**(Note: This section only applies to Zynq and ZynqMP.)**

### Preparing SD/MMC
Setup the card with the first partition formatted as FAT16. If you intend to
boot with the root filesystem located on the SD card, also create a second
partition formatted as EXT4.

It is recommended that the first partition be at least 64MB in size, however
this value will depend on whether using a ramdisk for the root filesystem and
how large the ramdisk is.

This section describes how to manually prepare and populate an SD card image.
There are automation tools in OpenEmbedded that can generate disk images already
formatted and prepared such that they can be written directly to a disk. Refer
to the Yocto Project Development Manual for more details:
	http://www.yoctoproject.org/docs/current/dev-manual/dev-manual.html#creating-partitioned-images

### Installing U-Boot (Zynq)
Add the following files to the first partition:

* `boot.bin`
* `u-boot.img`

### Installing U-Boot (ZynqMP)
Add the following files to the first partition:

* `boot.bin`
* `u-boot.bin`

### Installing Kernel and Device Tree (Zynq)
Add the following files to the first partition:

* `uImage`
* `<machine name>.dtb`

### Installing Kernel and Device Tree (ZynqMP)
Add the following files to the first partition:

* `Image`
* `<machine name>.dtb`

### Install ARM Trusted Firmware (ZynqMP)
Add the following file to the first partition:

 * `atf-uboot.ub`

### Install U-boot environment file (ZynqMP)
Add the following file to the first partition:

 * `uEnv.txt`

### Installing Root Filesystem
If using a ramdisk also add the `.cpio.gz.u-boot` type of root filesystem image
to the first partition.

* `core-image-minimal-<machine name>.cpio.gz.u-boot`

If using the SD card as the root filesystem, populate the second partition with
the content of the root filesystem. To install the root filesystem extract the
corresponding tarball into the root of the second partition (the following
command assumes that the second partition is mounted at /media/root).

	tar x -C /media/root -f core-image-minimal-<machine name>.tar.gz

### U-Boot Configuration File
Also create the file `uEnv.txt` on the first partition of the SD card partition,
with the following contents. Replacing the names of files where appropriate.

	kernel_image=uImage
	devicetree_image=<machine name>.dtb

If using a ramdisk root filesystem setup the `ramdisk_image` variable.

	ramdisk_image=core-image-minimal-<machine name>.cpio.gz.u-boot

If using the SD card as the root filesystem setup the kernel boot args, and
`uenvcmd` variable.

	bootargs=root=/dev/mmcblk0p2 rw rootwait
	uenvcmd=fatload mmc 0 0x3000000 ${kernel_image} && fatload mmc 0 0x2A00000 ${devicetree_image} && bootm 0x3000000 - 0x2A00000

### Booting
Insert the SD card and connect UART to a terminal program and power on the
board. (For boards that have configurable boot jumper/switches ensure the board
is configured for SD).

Initially U-Boot SPL will load, which will in turn load U-Boot. U-Boot will use
the `uEnv.txt` to automatically load and execute the kernel.


Loading via TFTP
----------------
**(Note: This boot flow requires ethernet on the baord and a TFTP server)**

Boot your system into U-Boot, using one of boot methods (e.g. JTAG, SD, QSPI).

### Kernel, Root Filesystem and Device Tree
Place the following images into the root of the TFTP server directory:

* `core-image-minimal-<machine name>.cpio.gz.u-boot`
* `uImage` (Zynq) or `linux.bin.ub` (MicroBlaze)
* `<machine name>.dtb`

### Booting via U-Boot
The serial console of the target board will display the U-Boot console.
Configure the `ipaddr` and `serverip` of the U-Boot environment.

	U-Boot> set serverip <server ip>
	U-Boot> set ipaddr <board ip>

Using the U-Boot console; load the Kernel, root filesystem and the DTB into
memory. And then boot Linux using the `bootm` command. (Note the load addresses
will be dependant on machine used)

MicroBlaze (kc705-microblazeel):

	U-Boot> tftpboot 0x85000000 linux.bin.ub
	U-Boot> tftpboot 0x86000000 core-image-minimal-kc705-microblazeel.cpio.gz.u-boot
	U-Boot> tftpboot 0x84000000 kc705-microblazeel.dtb
	U-Boot> bootm 0x85000000 0x86000000 0x84000000

Zynq:

	U-Boot> tftpboot 0x2000000 uImage
	U-Boot> tftpboot 0x3000000 core-image-minimal-<machine name>.cpio.gz.u-boot
	U-Boot> tftpboot 0x2A00000 <machine name>.dtb
	U-Boot> bootm 0x2000000 0x3000000 0x2A00000

U-Boot will prepare the Kernel for boot and then it will being to execute.

	...
	Starting kernel...

