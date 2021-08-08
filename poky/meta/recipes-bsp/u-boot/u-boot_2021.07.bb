require u-boot-common.inc
require u-boot.inc

SRC_URI:append = " file://0001-riscv32-Use-double-float-ABI-for-rv32.patch"

DEPENDS += "bc-native dtc-native python3-setuptools-native"
