FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " file://fb_host_settings.override.yml"
SRC_URI:append:fb-compute = " file://restrictionmode-host-settings.override.yml"

SETTINGS_HOST_TEMPLATES:append = " fb_host_settings.override.yml"
SETTINGS_HOST_TEMPLATES:append:fb-compute = " restrictionmode-host-settings.override.yml"
