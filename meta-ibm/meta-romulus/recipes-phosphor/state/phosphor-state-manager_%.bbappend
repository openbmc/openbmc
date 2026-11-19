FILESEXTRAPATHS:prepend:romulus := "${THISDIR}/${BPN}:"

EXTRA_OEMESON:romulus += "-Dwarm-reboot=disabled"
