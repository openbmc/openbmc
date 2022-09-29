FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# Multi Host Overrides
SRC_URI += "file://ibm_host_settings.override.yml"
SETTINGS_HOST_TEMPLATES:append = " ibm_host_settings.override.yml"
