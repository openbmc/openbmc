DESCRIPTION = "An image as an exmaple for Ima support"

IMAGE_FEATURES += "ssh-server-openssh"

LICENSE = "MIT"

inherit core-image

IMAGE_INSTALL += "\
    packagegroup-base \
    packagegroup-core-boot \
    packagegroup-ima-evm-utils \
    os-release"

export IMAGE_BASENAME = "integrity-image-minimal"

INHERIT += "ima-evm-rootfs"

QB_KERNEL_CMDLINE_APPEND:append = " ima_policy=tcb ima_appraise=fix"
