FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# enable SoL by default
SRC_URI:append = " file://sol_settings.override.yml"
