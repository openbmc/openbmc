require u-boot-common.inc
require u-boot.inc

SRC_URI +=       " file://0001-riscv32-Use-double-float-ABI-for-rv32.patch \
                   file://0001-riscv-fix-build-with-binutils-2.38.patch \
                   file://0001-i2c-fix-stack-buffer-overflow-vulnerability-in-i2c-m.patch \
                   file://0001-fs-squashfs-sqfs_read-Prevent-arbitrary-code-executi.patch \
                   file://0001-net-Check-for-the-minimum-IP-fragmented-datagram-siz.patch \
                   file://0001-fs-squashfs-Use-kcalloc-when-relevant.patch \
                 "

DEPENDS += "bc-native dtc-native python3-setuptools-native"

