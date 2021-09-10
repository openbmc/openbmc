FILESEXTRAPATHS:prepend:olympus-nuvoton := "${THISDIR}/${PN}:"

SRC_URI:append:olympus-nuvoton = " file://sol-default.override.yml"
