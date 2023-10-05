FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:p10bmc = " file://mctp"
SRC_URI:append:witherspoon-tacoma = " file://mctp"
