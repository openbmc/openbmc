INITRAMFS_CTYPE="zst"

IMAGE_FSTYPES += "cpio.${INITRAMFS_CTYPE}.fitImage mtd-static-norootfs"

# We don't want to build an initial kernel-based FIT image because the
# initramfs is going to be the eventual rootfs from this image and not
# a smaller initramfs.
INITRAMFS_IMAGE = ""
KERNEL_IMAGETYPE = "${ARCH_DEFAULT_KERNELIMAGETYPE}"
KERNEL_IMAGETYPES = "${ARCH_DEFAULT_KERNELIMAGETYPE}"
