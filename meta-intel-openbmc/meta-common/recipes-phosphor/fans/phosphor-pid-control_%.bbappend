FILESEXTRAPATHS_prepend_intel := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd
SYSTEMD_SERVICE_${PN}_intel = "phosphor-pid-control.service"
EXTRA_OECONF_intel = "--enable-configure-dbus=yes"
