#@TYPE: Machine
#@NAME: OpenBMC
#@DESCRIPTION: Common machine configuration for OpenBMC chips

KERNEL_CLASSES ?= "kernel-fitimage"
KERNEL_IMAGETYPES ?= "fitImage"
KERNEL_EXTRA_ARGS += "LOADADDR=${UBOOT_ENTRYPOINT}"

UBOOT_SUFFIX ?= "bin"

MACHINEOVERRIDES =. "openbmc:"

IMAGE_FSTYPES += "cpio.${INITRAMFS_CTYPE}.u-boot"
IMAGE_FSTYPES += "${@bb.utils.contains('MACHINE_FEATURES', \
        'obmc-ubi-fs', \
        'mtd-ubi mtd-ubi-tar', \
        'mtd-static mtd-static-tar mtd-static-alltar', d)}"
OBMC_IMAGE_EXTRA_INSTALL_append = " u-boot-fw-utils"

INITRAMFS_CTYPE ?= "lzma"
INITRAMFS_FSTYPES += "cpio.${INITRAMFS_CTYPE}"
INITRAMFS_IMAGE = "${@bb.utils.contains('MACHINE_FEATURES', \
        'obmc-ubi-fs', '', 'obmc-phosphor-initramfs', d)}"

MACHINE_FEATURES_BACKFILL_CONSIDERED = "qemu-usermode"

