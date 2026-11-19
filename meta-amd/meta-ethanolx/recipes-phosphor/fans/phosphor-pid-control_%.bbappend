FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

inherit obmc-phosphor-systemd

SYSTEMD_SERVICE:${PN} = "phosphor-pid-control.service"
