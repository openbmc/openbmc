FILESEXTRAPATHS:append:gbs := ":${THISDIR}/${PN}"
SRC_URI:append:gbs = " file://restrictionmode-default-whitelist.override.yml"
