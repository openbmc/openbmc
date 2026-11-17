FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append = " file://restrictionmode-ocpdebugcard-settings.override.yml"
