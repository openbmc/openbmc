DESCRIPTION = "An image as an exmaple for Ima support"

IMAGE_FEATURES += "ssh-server-openssh"


IMAGE_INSTALL = "\
    packagegroup-base \
    packagegroup-core-boot \
    packagegroup-ima-evm-utils \
    os-release"


LICENSE = "MIT"

inherit core-image

export IMAGE_BASENAME = "integrity-image-minimal"

INHERIT += "ima-evm-rootfs"

QB_KERNEL_CMDLINE_APPEND_append = " ima_appraise=fix ima_policy=tcb ima_policy=appraise_tcb"
