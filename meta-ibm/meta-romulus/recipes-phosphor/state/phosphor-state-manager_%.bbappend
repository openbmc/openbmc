FILESEXTRAPATHS:prepend:romulus := "${THISDIR}/${PN}:"

EXTRA_OEMESON:romulus += "-Dwarm-reboot=disabled"
