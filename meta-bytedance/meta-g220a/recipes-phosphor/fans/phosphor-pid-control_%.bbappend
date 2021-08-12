FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd

SYSTEMD_SERVICE:${PN} = "phosphor-pid-control.service"
