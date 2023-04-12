FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG:append:daytonax = " boot_type"

SRC_URI += " file://sol-default.override.yml"
