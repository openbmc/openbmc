FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

# utilize a 600s default timer config for p10bmc
SYSTEMD_ENVIRONMENT_FILE_${PN}_append_p10bmc = " obmc/watchdog/poweron"
