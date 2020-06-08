SUMMARY = "meta-initramfs build test image"

IMAGE_INSTALL = "packagegroup-core-boot \
                 packagegroup-meta-initramfs"

inherit core-image
