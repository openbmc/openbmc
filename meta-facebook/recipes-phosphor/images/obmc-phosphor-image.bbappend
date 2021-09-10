FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

IMAGE_FEATURES:remove:fb-nohost = "obmc-console"

OBMC_IMAGE_EXTRA_INSTALL:append = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'tpm', \
        bb.utils.contains('MACHINE_FEATURES', 'tpm2', 'tpm2-tools', '', d), \
        '', d)} \
    "
