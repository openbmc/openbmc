FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append:ibm-ac-server = " file://witherspoon.cfg"
SRC_URI:append:p10bmc = " file://p10bmc.cfg"
