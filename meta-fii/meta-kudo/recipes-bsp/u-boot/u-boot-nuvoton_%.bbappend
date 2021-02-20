FILESEXTRAPATHS_prepend_kudo := "${THISDIR}/u-boot-nuvoton:"

UBOOT_MAKE_TARGET_append_kudo = " DEVICE_TREE=${UBOOT_DEVICETREE}"

SRC_URI_append_kudo = " file://kudo.cfg"
