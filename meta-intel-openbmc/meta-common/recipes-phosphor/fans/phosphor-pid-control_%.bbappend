FILESEXTRAPATHS:prepend:intel := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd
SYSTEMD_SERVICE:${PN}:intel = "phosphor-pid-control.service"
EXTRA_OECONF:intel = "--enable-configure-dbus=yes"
