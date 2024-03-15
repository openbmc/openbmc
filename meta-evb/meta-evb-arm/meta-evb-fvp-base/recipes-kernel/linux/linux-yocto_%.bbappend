FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

COMPATIBLE_MACHINE = "fvp"

SRC_URI:append = " \
file://defconfig \
"
