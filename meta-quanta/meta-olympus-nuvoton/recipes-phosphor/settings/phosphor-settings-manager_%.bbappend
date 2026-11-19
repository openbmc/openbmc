FILESEXTRAPATHS:prepend:olympus-nuvoton := "${THISDIR}/${BPN}:"

SRC_URI:append:olympus-nuvoton = " file://sol-default.override.yml"
