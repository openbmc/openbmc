FILESEXTRAPATHS:append:mori := ":${THISDIR}/${PN}"

SRC_URI:append:mori = " file://chassis_capabilities.override.yml"
