DESCRIPTION = "A small image for building meta-security packages"

IMAGE_FEATURES += "ssh-server-openssh"

IMAGE_INSTALL = "\
    ${@bb.utils.contains("DISTRO_FEATURES", "lkrg", "lkrg-module", "",d)} \
    packagegroup-base \
    packagegroup-core-boot \
    packagegroup-core-security \
    os-release" 

IMAGE_LINGUAS ?= " "

LICENSE = "MIT"

inherit core-image

export IMAGE_BASENAME = "security-build-image"

IMAGE_ROOTFS_EXTRA_SPACE = "5242880"

QB_KERNEL_CMDLINE_APPEND = " ${@bb.utils.contains('DISTRO_FEATURES', 'apparmor', 'apparmor=1 security=apparmor', '', d)}"

# We need more mem to run many apps in this layer
QB_MEM = "-m 2048"
