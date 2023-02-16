DESCRIPTION = "Linux Kernel for Raspberry Pi"
SECTION = "kernel"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

COMPATIBLE_MACHINE ?= "^rpi$"

PE = "1"
PV = "${LINUX_VERSION}+git${SRCPV}"

inherit siteinfo
require recipes-kernel/linux/linux-yocto.inc

SRC_URI += " \
    ${@bb.utils.contains("INITRAMFS_IMAGE_BUNDLE", "1", "file://initramfs-image-bundle.cfg", "", d)} \
    ${@bb.utils.contains("MACHINE_FEATURES", "vc4graphics", "file://vc4graphics.cfg", "", d)} \
    ${@bb.utils.contains("MACHINE_FEATURES", "wm8960", "file://wm8960.cfg", "", d)} \
    "

SRC_URI:append:raspberrypi4 = " \
    file://rpi4-nvmem.cfg \
"

KCONFIG_MODE = "--alldefconfig"
KBUILD_DEFCONFIG:raspberrypi0-wifi ?= "bcmrpi_defconfig"
KBUILD_DEFCONFIG:raspberrypi ?= "bcmrpi_defconfig"
KBUILD_DEFCONFIG:raspberrypi-cm3 ?= "bcm2709_defconfig"
KBUILD_DEFCONFIG:raspberrypi2 ?= "bcm2709_defconfig"
KBUILD_DEFCONFIG:raspberrypi3 ?= "bcm2709_defconfig"
KBUILD_DEFCONFIG:raspberrypi3-64 ?= "bcmrpi3_defconfig"
KBUILD_DEFCONFIG:raspberrypi4 ?= "bcm2711_defconfig"
KBUILD_DEFCONFIG:raspberrypi4-64 ?= "bcm2711_defconfig"
KBUILD_DEFCONFIG:raspberrypi-armv7 ?= "bcm2711_defconfig"
KBUILD_DEFCONFIG:raspberrypi-armv8 ?= "bcm2711_defconfig"

LINUX_VERSION_EXTENSION ?= ""

KERNEL_MODULE_AUTOLOAD += "${@bb.utils.contains("MACHINE_FEATURES", "pitft28r", "stmpe-ts", "", d)}"

# A LOADADDR is needed when building a uImage format kernel. This value is not
# set by default in rpi-4.8.y and later branches so we need to provide it
# manually. This value unused if KERNEL_IMAGETYPE is not uImage.
KERNEL_EXTRA_ARGS += "LOADADDR=${UBOOT_ENTRYPOINT}"

UBOOT_ENTRYPOINT =       "0x00008000"
UBOOT_LOADADDRESS =      "0x00008000"
