FILESEXTRAPATHS:prepend:gbmc := "${THISDIR}/${PN}:"

SRC_URI:append:gbmc = " \
  file://rsyslog.conf \
"

PACKAGECONFIG:append:gbmc = " imjournal"
