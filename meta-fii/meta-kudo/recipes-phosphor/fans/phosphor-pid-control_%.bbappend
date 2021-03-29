FILESEXTRAPATHS_prepend_kudo := "${THISDIR}/${PN}:"
SRC_URI_append_kudo = " file://phosphor-pid-control.service"

inherit systemd

SYSTEMD_SERVICE_${PN}_append_kudo = " phosphor-pid-control.service"
