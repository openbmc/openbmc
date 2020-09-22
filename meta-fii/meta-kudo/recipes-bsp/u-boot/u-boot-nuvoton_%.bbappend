FILESEXTRAPATHS_prepend_kudo := "${THISDIR}/u-boot-nuvoton:"

require u-boot-common-kudo.inc

UBOOT_MAKE_TARGET_append_kudo = " DEVICE_TREE=${UBOOT_DEVICETREE}"

SRC_URI_append_kudo = " \
			file://fixed_phy.cfg \
			file://kudo.cfg \
			"
