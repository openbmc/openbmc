#@TYPE: Machine
#@NAME: OpenBMC
#@DESCRIPTION: Common machine configuration for OpenBMC chips

KERNEL_CLASSES ?= "kernel-fitimage"
KERNEL_IMAGETYPES ?= "fitImage"
KERNEL_EXTRA_ARGS += "LOADADDR=${UBOOT_ENTRYPOINT}"

UBOOT_SUFFIX ?= "bin"

MACHINEOVERRIDES =. "openbmc:"

IMAGE_FSTYPES += "cpio.${INITRAMFS_CTYPE}.u-boot overlay"
OBMC_IMAGE_EXTRA_INSTALL_append = " u-boot-fw-utils"

INITRAMFS_CTYPE ?= "lzma"
INITRAMFS_FSTYPES += "cpio.${INITRAMFS_CTYPE}"
INITRAMFS_IMAGE = "obmc-phosphor-initramfs"

MACHINE_FEATURES_BACKFILL_CONSIDERED = "qemu-usermode"

