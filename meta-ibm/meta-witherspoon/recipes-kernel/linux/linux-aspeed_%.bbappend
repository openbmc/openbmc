FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://witherspoon.cfg"
SRC_URI_append_witherspoon-128 = " file://0001-ARM-dts-Aspeed-Witherspoon-128-Update-BMC-partitioni.patch"
