SUMMARY = "meta-initramfs build test image"

IMAGE_INSTALL = "packagegroup-core-boot \
                 packagegroup-meta-initramfs"

LICENSE = "MIT"

inherit core-image
