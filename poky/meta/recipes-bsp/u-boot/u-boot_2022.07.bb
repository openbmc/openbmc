require u-boot-common.inc
require u-boot.inc

SRC_URI +=       " file://0001-riscv32-Use-double-float-ABI-for-rv32.patch \
                   file://0001-riscv-fix-build-with-binutils-2.38.patch \
                 "

DEPENDS += "bc-native dtc-native python3-setuptools-native"

