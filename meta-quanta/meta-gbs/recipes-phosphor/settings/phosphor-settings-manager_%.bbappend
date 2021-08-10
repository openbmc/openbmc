FILESEXTRAPATHS:append:gbs := ":${THISDIR}/${PN}"
SRC_URI:append:gbs = " file://time-default.override.yml"
SRC_URI:append:gbs = " file://restrictionmode-default-whitelist.override.yml"
