#Override the default time settings from BMC/NTP to Host/Manual, so that host
#can set BMC time via an IPMI command.

FILESEXTRAPATHS_append_gbmc := ":${THISDIR}/${PN}"
SRC_URI_append_gbmc = " file://timemanager-default-HOST-MANUAL.override.yml"
