FILESEXTRAPATHS_prepend := "${THISDIR}/linux-obmc:"
SRC_URI += "file://witherspoon.cfg"
SRC_URI += "file://0004-ARM-dts-aspeed-Configure-Witherspoon-to-mark-its-fan.patch"
SRC_URI += "file://0005-max31785-Support-mark-fan-installed-devicetree-prope.patch"
