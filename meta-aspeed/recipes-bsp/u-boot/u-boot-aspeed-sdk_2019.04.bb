require u-boot-common-aspeed-sdk_${PV}.inc

UBOOT_MAKE_TARGET ?= "DEVICE_TREE=${UBOOT_DEVICETREE}"

require u-boot-aspeed.inc

PROVIDES += "u-boot"
DEPENDS += "bc-native dtc-native"
