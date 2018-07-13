DESCRIPTION = "A small image for building meta-security packages"

IMAGE_FEATURES += "ssh-server-openssh"

IMAGE_INSTALL = "\
    packagegroup-base \
    packagegroup-core-boot \
    ${@bb.utils.contains('MACHINE_FEATURES', 'tpm',  'packagegroup-security-tpm',  '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'tpm2', 'packagegroup-security-tpm2', '', d)} \
    os-release \
    ${CORE_IMAGE_EXTRA_INSTALL}"

IMAGE_LINGUAS ?= " "

LICENSE = "MIT"

inherit core-image

export IMAGE_BASENAME = "security-tpm-image"
