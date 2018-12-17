DESCRIPTION = "A small image for building meta-security packages"

IMAGE_FEATURES += "ssh-server-openssh"

IMAGE_INSTALL = "\
    packagegroup-base \
    packagegroup-core-boot \
    packagegroup-core-security \
    os-release" 

IMAGE_LINGUAS ?= " "

LICENSE = "MIT"

inherit core-image

export IMAGE_BASENAME = "security-build-image"

IMAGE_ROOTFS_EXTRA_SPACE = "5242880"
