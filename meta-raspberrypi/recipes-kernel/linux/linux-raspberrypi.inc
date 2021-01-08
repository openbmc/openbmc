DESCRIPTION = "Linux Kernel for Raspberry Pi"
SECTION = "kernel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

COMPATIBLE_MACHINE = "^rpi$"

PE = "1"
PV = "${LINUX_VERSION}+git${SRCPV}"

inherit kernel siteinfo
require recipes-kernel/linux/linux-yocto.inc

SRC_URI += " \
    ${@bb.utils.contains("INITRAMFS_IMAGE_BUNDLE", "1", "file://initramfs-image-bundle.cfg", "", d)} \
    ${@bb.utils.contains("MACHINE_FEATURES", "vc4graphics", "file://vc4graphics.cfg", "", d)} \
    "

KCONFIG_MODE = "--alldefconfig"
KBUILD_DEFCONFIG_raspberrypi0-wifi ?= "bcmrpi_defconfig"
KBUILD_DEFCONFIG_raspberrypi ?= "bcmrpi_defconfig"
KBUILD_DEFCONFIG_raspberrypi-cm3 ?= "bcm2709_defconfig"
KBUILD_DEFCONFIG_raspberrypi2 ?= "bcm2709_defconfig"
KBUILD_DEFCONFIG_raspberrypi3 ?= "bcm2709_defconfig"
KBUILD_DEFCONFIG_raspberrypi3-64 ?= "bcmrpi3_defconfig"
KBUILD_DEFCONFIG_raspberrypi4 ?= "bcm2711_defconfig"
KBUILD_DEFCONFIG_raspberrypi4-64 ?= "bcm2711_defconfig"

LINUX_VERSION_EXTENSION ?= ""

KERNEL_MODULE_AUTOLOAD += "${@bb.utils.contains("MACHINE_FEATURES", "pitft28r", "stmpe-ts", "", d)}"

# A LOADADDR is needed when building a uImage format kernel. This value is not
# set by default in rpi-4.8.y and later branches so we need to provide it
# manually. This value unused if KERNEL_IMAGETYPE is not uImage.
KERNEL_EXTRA_ARGS += "LOADADDR=0x00008000"
