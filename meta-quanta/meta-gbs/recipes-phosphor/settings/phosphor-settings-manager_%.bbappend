FILESEXTRAPATHS_append_gbs := ":${THISDIR}/${PN}"
SRC_URI_append_gbs = " file://time-default.override.yml"
SRC_URI_append_gbs = " file://restrictionmode-default-whitelist.override.yml"
