FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:p10bmc = " file://mctp"
SRC_URI:append:witherspoon-tacoma = " file://mctp"

PACKAGECONFIG:append:p10bmc = " astlpc-raw-kcs"
PACKAGECONFIG:append:witherspoon-tacoma = " astlpc-raw-kcs"
