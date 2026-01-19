# Copyright (c) 2021-24 Axiado Corporation (or its affiliates). All rights reserved.

SOC_FAMILY = "axiado-ax3000"
include conf/machine/include/soc-family.inc
include conf/machine/include/arm/arch-arm64.inc

require VERSION.inc
LATEST_RELEASE_VERSION = "release/release-${COMPONENT_VERSION_MAJOR}.${COMPONENT_VERSION_MINOR}.${COMPONENT_PATCHLEVEL}"

PREFERRED_PROVIDER_virtual/kernel ?= "linux-axiado"
PREFERRED_VERSION_linux-axiado ?= "6.6.16+git"
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-axiado"
PREFERRED_PROVIDER_u-boot ?= "u-boot-axiado"
PREFERRED_VERSION_u-boot-axiado ?= "2022.01+git"
PREFERRED_PROVIDER_u-boot-fw-utils ?= "libubootenv"

INITRAMFS_CTYPE ?= "xz"
INITRAMFS_FSTYPES = "cpio.${INITRAMFS_CTYPE}"
IMAGE_FSTYPES = "${INITRAMFS_FSTYPES}"

KERNEL_CLASSES ?= "kernel-fitimage"

# Build var, valid values are ["dbg", "dev", "prod"]
# Dbg builds initramfs image with filesystem + kernel in a single image and u-boot
# Dev builds kernel-fitImage and eMMC image with wic filesystem and u-boot
# Prod builds kernel-fitImage and eMMC image with wic filesystem and Axiado-signed u-boot
# For modifying this value, add BUILD_VAR = "dev" in local.conf file under build/<machine>
BUILD_VAR ??= "prod"
require conf/machine/include/axiado-${BUILD_VAR}.inc

INITRAMFS_IMAGE_BUNDLE = "1"

DEFAULTTUNE:ax3000 ?= "aarch64"

KERNEL_IMAGETYPES ?= "${KERNEL_IMAGETYPE}"

UBOOT_ENTRYPOINT ?= "0x3E000000"
UBOOT_LOADADDRESS ?= "0x3E000000"
UBOOT_DTB_LOADADDRESS ?= "0x3FF00000"

#this is needed by emmc, avoid removing it.
BAD_RECOMMENDATIONS:remove = "e2fsprogs-e2fsck"
