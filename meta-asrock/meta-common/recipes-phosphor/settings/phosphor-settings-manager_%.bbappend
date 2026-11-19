FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

# enable SoL by default
SRC_URI:append = " file://sol_settings.override.yml"
