FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"
SRC_URI:append:ncplite = " file://ncplite_settings.override.yml"
