#@TYPE: Machine
#@NAME: OpenBMC
#@DESCRIPTION: Common machine configuration for OpenBMC chips

KERNEL_CLASSES ?= "kernel-fitimage"
KERNEL_IMAGETYPES ?= "fitImage"

# Static images. This is the default if no other layout is specified.
IMAGE_FSTYPES += "${@bb.utils.contains_any('DISTRO_FEATURES', \
        'obmc-ubi-fs phosphor-mmc', \
        '', \
        'mtd-static mtd-static-tar mtd-static-alltar', d)}"

# UBI images.
IMAGE_FSTYPES += "${@bb.utils.contains('DISTRO_FEATURES', \
        'obmc-ubi-fs', \
        'mtd-ubi mtd-ubi-tar', \
        '', d)}"

# eMMC images.
IMAGE_FSTYPES += "${@bb.utils.contains('DISTRO_FEATURES', \
        'phosphor-mmc', \
        'wic.xz mmc-ext4-tar', \
        '', d)}"

INITRAMFS_CTYPE ?= "xz"
INITRAMFS_FSTYPES = "cpio.${INITRAMFS_CTYPE}"
INITRAMFS_IMAGE = "obmc-phosphor-initramfs"
INITRAMFS_IMAGE_df-obmc-ubi-fs = ""
