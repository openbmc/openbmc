FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:mf-fb-secondary-emmc = " \
    file://emmc-btrfs.scc \
    "

KERNEL_FEATURES:append:mf-fb-secondary-emmc = " \
    emmc-btrfs.scc \
    "
