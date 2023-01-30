FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:qemuarm64-secureboot = " file://qemuarm64.cfg"
SRC_URI:append:qemuarm-secureboot = " \
    file://0001-qemu-arm-make-QFW-MMIO-implied-on-qemu-arm.patch \
    file://qemuarm.cfg \
    "
