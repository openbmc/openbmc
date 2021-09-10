DESCRIPTION = "A Serve side image for Security example "

IMAGE_FEATURES += "ssh-server-openssh"

IMAGE_INSTALL = "\
    packagegroup-base \
    packagegroup-core-boot \
    samhain-server \
    os-release "

IMAGE_LINGUAS ?= " "

LICENSE = "MIT"

inherit core-image

export IMAGE_BASENAME = "security-server-image"

IMAGE_ROOTFS_EXTRA_SPACE = "5242880"
