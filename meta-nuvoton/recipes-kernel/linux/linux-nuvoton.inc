DESCRIPTION = "Linux kernel for Nuvoton NPCM7xx"
SECTION = "kernel"
LICENSE = "GPL-2.0-only"

PROVIDES += "virtual/kernel"

KCONFIG_MODE="--alldefconfig"

KSRC ?= "git://github.com/Nuvoton-Israel/linux;protocol=https;branch=${KBRANCH}"
SRC_URI = "${KSRC}"
SRC_URI:append:npcm7xx = " file://defconfig"
SRC_URI:append:npcm8xx = " file://npcm8xx_defconfig"

LINUX_VERSION_EXTENSION ?= "-${SRCREV}"

PV = "${LINUX_VERSION}+git${SRCPV}"

inherit kernel
require recipes-kernel/linux/linux-yocto.inc

# From 4.16+ the COPYING file changed
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"
