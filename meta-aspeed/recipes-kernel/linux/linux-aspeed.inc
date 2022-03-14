DESCRIPTION = "Linux kernel for Aspeed"
SECTION = "kernel"
LICENSE = "GPLv2"

PROVIDES += "virtual/kernel"

KCONFIG_MODE="--alldefconfig"

KSRC ?= "git://github.com/openbmc/linux;protocol=https;branch=${KBRANCH}"
SRC_URI += "${KSRC}"
SRC_URI += " \
             file://defconfig \
             file://rsa_oem_fitimage_key.key;sha256sum=eeb4ff2ebbfbd97b6254fe6dbaeea41067e54c65176c233ec7b2ab2decf1ddcd \
             file://rsa_oem_fitimage_key.crt;sha256sum=45f5a55497cce8040999bf9f3214d471ac7b83ab7acef41c4425a34662e8372e \
             ${@bb.utils.contains('MACHINE_FEATURES', 'tpm2', 'file://tpm/tpm2.scc', '', d)} \
           "

LINUX_VERSION_EXTENSION ?= "-${SRCREV}"

PV = "${LINUX_VERSION}+git${SRCPV}"

inherit kernel
require recipes-kernel/linux/linux-yocto.inc

# From 5.6+ the COPYING file changed
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"
