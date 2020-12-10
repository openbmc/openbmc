FILESEXTRAPATHS_prepend_mtjade := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://${MACHINE}.cfg \
    file://0001-ARM-dts-aspeed-Add-device-tree-for-Ampere-s-Mt.-Jade.patch \
"

