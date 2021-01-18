FILESEXTRAPATHS_prepend_mtjade := "${THISDIR}/${PN}:"

SRC_URI = "git://github.com/ampere-openbmc/linux;protocol=git;branch=ampere"
SRC_URI += " file://defconfig"
SRC_URI += " file://${MACHINE}.cfg"

SRCREV="95e7592631f7033cc771306195805e32f0a7504b"
