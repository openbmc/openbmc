DESCRIPTION = "A Client side Security example"

IMAGE_INSTALL = "\
    packagegroup-base \
    packagegroup-core-boot \
    os-release \
    samhain-client \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "packagegroup-xfce-base", "", d)} \
    ${ROOTFS_PKGMANAGE_BOOTSTRAP} ${CORE_IMAGE_EXTRA_INSTALL}"

IMAGE_LINGUAS ?= " "

LICENSE = "MIT"

inherit core-image

export IMAGE_BASENAME = "security-client-image"
