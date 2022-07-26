FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://fb_host_settings.override.yml"
SETTINGS_HOST_TEMPLATES:append = " fb_host_settings.override.yml"
