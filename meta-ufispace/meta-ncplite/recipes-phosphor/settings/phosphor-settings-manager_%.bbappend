FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append:ncplite = " file://ncplite_settings.override.yml"
