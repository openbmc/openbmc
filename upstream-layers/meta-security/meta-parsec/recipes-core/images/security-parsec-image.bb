DESCRIPTION = "A small image for testing Parsec service with MbedCrypto, TPM and PKCS11 providers"

inherit core-image

IMAGE_FEATURES += "ssh-server-openssh"

IMAGE_INSTALL = "\
    packagegroup-base \
    packagegroup-core-boot \
    packagegroup-security-tpm2 \
    packagegroup-security-parsec \
    swtpm \
    softhsm \
    os-release"

export IMAGE_BASENAME = "security-parsec-image"

IMAGE_ROOTFS_EXTRA_SPACE = "5242880"
