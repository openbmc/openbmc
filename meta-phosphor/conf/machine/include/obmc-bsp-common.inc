#@TYPE: Machine
#@NAME: OpenBMC
#@DESCRIPTION: Common machine configuration for OpenBMC chips

MACHINE_EXTRA_RRECOMMENDS = " kernel-modules"
EXTRA_IMAGEDEPENDS += "u-boot"

KERNEL_CLASSES ?= "kernel-fitimage"
KERNEL_IMAGETYPES ?= "fitImage"
KERNEL_EXTRA_ARGS += "LOADADDR=${UBOOT_ENTRYPOINT}"

UBOOT_SUFFIX ?= "bin"

MACHINEOVERRIDES =. "openbmc:"

OBMC_PHOSPHOR_IMAGE_OVERLAY= "1"
OBMC_IMAGE_EXTRA_INSTALL_append = " u-boot-fw-utils"

IMAGE_CLASSES += "image_types_uboot"

INITRAMFS_CTYPE ?= "lzma"
INITRAMFS_FSTYPES += "cpio.${INITRAMFS_CTYPE}.u-boot"

MACHINE_FEATURES_BACKFILL_CONSIDERED = "qemu-usermode"

