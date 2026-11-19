FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

PACKAGECONFIG:append:daytonax = " boot_type"

SRC_URI += " file://sol-default.override.yml"
