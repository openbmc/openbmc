FILESEXTRAPATHS_prepend_mtjade := "${THISDIR}/${PN}:"

SRC_URI = "git://github.com/ampere-openbmc/linux;protocol=git;branch=ampere"
SRC_URI += " file://defconfig"
SRC_URI += " file://${MACHINE}.cfg"

SRCREV="b286305d3e2ad47d74d5bdb4f034d4f479fcdda5"
