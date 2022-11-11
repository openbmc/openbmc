FILESEXTRAPATHS:prepend:gsj := "${THISDIR}/${BPN}:"
SRC_URI:append:gsj = " file://restrictionmode-default-whitelist.override.yml"
