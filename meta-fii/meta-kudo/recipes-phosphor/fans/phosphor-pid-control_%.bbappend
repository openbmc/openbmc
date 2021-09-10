FILESEXTRAPATHS:prepend:kudo := "${THISDIR}/${PN}:"
SRC_URI:append:kudo = " file://phosphor-pid-control.service"

inherit systemd

SYSTEMD_SERVICE:${PN}:append:kudo = " phosphor-pid-control.service"
