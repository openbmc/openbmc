FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd

SYSTEMD_SERVICE_${PN} = "phosphor-pid-control.service"
