FILESEXTRAPATHS:prepend:gsj := "${THISDIR}/${BPN}:"
SRC_URI:append:gsj = " file://time-default.override.yml"
SRC_URI:append:gsj = " file://restrictionmode-default-whitelist.override.yml"
