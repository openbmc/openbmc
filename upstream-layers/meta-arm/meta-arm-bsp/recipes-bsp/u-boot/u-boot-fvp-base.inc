# FVP base specific U-boot support

SRC_URI:append = " \
    file://virtio-boot.cfg \
    file://0003-vexpress64-Imply-CONFIG_ARM64_CRC32-by-default.patch \
    "
