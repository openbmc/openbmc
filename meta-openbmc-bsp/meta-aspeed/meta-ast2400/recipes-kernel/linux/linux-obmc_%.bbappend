FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://defconfig"
SRC_URI += "file://0001-work-around-aspeed-bmc-opp-palmetto-Add-GPIO-hogs-to.patch"
