FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# Remove, from the p10bmc image, the service file that starts the skeleton power
# control application. That image will use the power control application
# included in the phosphor-power repository.
DBUS_SERVICE:${PN}:remove:p10bmc = "${@compose_list(d, 'FMT', 'OBMC_POWER_INSTANCES')}"
