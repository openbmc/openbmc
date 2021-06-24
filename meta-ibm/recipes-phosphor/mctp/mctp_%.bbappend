FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI_append_p10bmc = " file://mctp"
SRC_URI_append_witherspoon-tacoma = " file://mctp"

PACKAGECONFIG_append_p10bmc = " astlpc-raw-kcs"
