#@TYPE: Machine
#@NAME: OpenBMC
#@DESCRIPTION: Common machine configuration for OpenBMC chips

KERNEL_CLASSES ?= "kernel-fitimage"
KERNEL_IMAGETYPES ?= "fitImage"

IMAGE_FSTYPES += "${@bb.utils.contains('DISTRO_FEATURES', \
        'obmc-ubi-fs', \
        'mtd-ubi mtd-ubi-tar', \
        'mtd-static mtd-static-tar mtd-static-alltar', d)}"

INITRAMFS_CTYPE ?= "lzma"
INITRAMFS_FSTYPES = "cpio.${INITRAMFS_CTYPE}"
INITRAMFS_IMAGE = "obmc-phosphor-initramfs"
INITRAMFS_IMAGE_df-obmc-ubi-fs = ""
