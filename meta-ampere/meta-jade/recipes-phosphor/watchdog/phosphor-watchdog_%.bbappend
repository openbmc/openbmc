FILESEXTRAPATHS:append:mtjade := "${THISDIR}/${PN}:"

#
# Ampere Mt. Jade power control involves different action during
# the course of action which does not involve rebooting the Host.
# Thus disable the use of Host Power-On watchdog.
#
WATCHDOG_FMT = ""
ENABLE_WATCHDOG_FMT = ""
