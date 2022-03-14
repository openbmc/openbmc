FILESEXTRAPATHS:prepend:gbmc := "${THISDIR}/files:"
SRC_URI:append:gbmc = " file://gbmc.cfg"
SRC_URI:remove:gbmc = "file://syslog.cfg"
