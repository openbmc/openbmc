#@TYPE: Machine
#@NAME: OpenBMC
#@DESCRIPTION: Common machine configuration for OpenBMC chips

# FIT images are now built by the linux-yocto-fitimage recipe
KERNEL_CLASSES ?= "kernel-fit-extra-artifacts"
KERNEL_IMAGETYPE ?= "zImage"
KERNEL_IMAGETYPES ?= "zImage"

FLASH_SIZE ?= "32768"

OBMC_IMAGE_INCLUDE = "static"
OBMC_IMAGE_INCLUDE:df-obmc-static-norootfs = "static-norootfs"
OBMC_IMAGE_INCLUDE:df-obmc-ubi-fs = "ubi-fs"
OBMC_IMAGE_INCLUDE:df-phosphor-mmc = "mmc"

require conf/machine/include/image-type/${OBMC_IMAGE_INCLUDE}.inc

INITRAMFS_CTYPE ?= "xz"
INITRAMFS_FSTYPES = "cpio.${INITRAMFS_CTYPE}"
