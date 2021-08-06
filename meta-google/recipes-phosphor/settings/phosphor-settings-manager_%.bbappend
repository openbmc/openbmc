#Override the default time settings from BMC/NTP to Host/Manual, so that host
#can set BMC time via an IPMI command.

FILESEXTRAPATHS:append:gbmc := ":${THISDIR}/${PN}"
SRC_URI:append:gbmc = " file://timemanager-default-HOST-MANUAL.override.yml"
