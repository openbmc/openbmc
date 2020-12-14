FILESEXTRAPATHS_prepend_mtjade := "${THISDIR}/${PN}:"

SRC_URI = "git://github.com/ampere-openbmc/linux;protocol=git;branch=ampere"
SRC_URI += " file://defconfig"
SRC_URI += " file://${MACHINE}.cfg"

SRCREV="21755c6f4ced36cdc52bc2b4614c1d534b9ca9b5"
