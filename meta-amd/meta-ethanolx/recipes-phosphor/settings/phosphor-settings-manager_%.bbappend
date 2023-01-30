FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG:append:ethanolx = " boot_type"

SRC_URI += " file://sol-default.override.yml"
