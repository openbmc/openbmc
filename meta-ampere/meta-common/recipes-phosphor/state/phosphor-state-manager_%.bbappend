FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

DEPS_CFG = "resetreason.conf"
DEPS_TGT = "phosphor-discover-system-state@.service"
SYSTEMD_OVERRIDE_${PN}-discover_append = "${DEPS_CFG}:${DEPS_TGT}.d/${DEPS_CFG}"

# We don't want the obmc-host-shutdown (softoff) to require
# obmc-chassis-poweroff. obmc-chassis-poweroff will be activated once
# the Shutdown ACK pin is toggled (monitored by phosphor-gpio-monitor)
HOST_STOP_FMT = ""
HOST_REBOOT_FMT = ""
