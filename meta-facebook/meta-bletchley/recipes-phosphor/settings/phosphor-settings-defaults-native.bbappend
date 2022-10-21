FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " file://bletchley-host-acpi-power-state.yaml"
SETTINGS_HOST_TEMPLATES:append = " bletchley-host-acpi-power-state.yaml"
