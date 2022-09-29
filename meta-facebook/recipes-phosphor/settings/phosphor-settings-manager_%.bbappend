FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " file://restrictionmode-ocpdebugcard-settings.override.yml"
