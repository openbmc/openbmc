DESCRIPTION = "A XFCE minimal demo image."

IMAGE_INSTALL = "packagegroup-core-boot \
    ${ROOTFS_PKGMANAGE_BOOTSTRAP} \
    packagegroup-core-x11 \
    packagegroup-xfce-base \
"

REQUIRED_DISTRO_FEATURES = "x11"

IMAGE_LINGUAS ?= " "

LICENSE = "MIT"

export IMAGE_BASENAME = "core-image-minimal-xfce"

inherit core-image
