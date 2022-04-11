FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append = " file://bletchley_settings.override.yml"

SETTINGS_HOST_TEMPLATES:append = " bletchley_settings.override.yml"
