FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

PACKAGECONFIG:append:ethanolx = " boot_type"

SRC_URI += " file://sol-default.override.yml"
