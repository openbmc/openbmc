FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append:mtjade = " file://mtjade_settings.override.yml"
