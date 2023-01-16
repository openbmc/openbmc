#@TYPE: Machine
#@NAME: OpenBMC
#@DESCRIPTION: Common machine configuration for OpenBMC chips

KERNEL_CLASSES ?= "kernel-fitimage"
KERNEL_IMAGETYPES ?= "fitImage vmlinux"

OBMC_IMAGE_INCLUDE = "static"
OBMC_IMAGE_INCLUDE:df-obmc-static-norootfs = "static-norootfs"
OBMC_IMAGE_INCLUDE:df-obmc-ubi-fs = "ubi-fs"
OBMC_IMAGE_INCLUDE:df-phosphor-mmc = "mmc"

require conf/machine/include/image-type/${OBMC_IMAGE_INCLUDE}.inc

INITRAMFS_CTYPE ?= "xz"
INITRAMFS_FSTYPES = "cpio.${INITRAMFS_CTYPE}"
