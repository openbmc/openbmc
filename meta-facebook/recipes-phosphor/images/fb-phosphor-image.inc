IMAGE_FEATURES:remove:fb-nohost = "obmc-console"

OBMC_IMAGE_EXTRA_INSTALL:append = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'tpm', \
        bb.utils.contains('MACHINE_FEATURES', 'tpm2', 'packagegroup-security-tpm2 libtss2-tcti-device', '', d), \
        '', d)} \
    "

OBMC_IMAGE_EXTRA_INSTALL:append:mf-fb-secondary-emmc = " emmc-init"

include recipes-phosphor/images/fb-${MACHINE}-phosphor-image.inc
