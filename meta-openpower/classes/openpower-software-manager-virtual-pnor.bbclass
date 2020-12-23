PACKAGECONFIG_append = " virtual_pnor"

SYSTEMD_SERVICE_${PN} += " \
        obmc-vpnor-updatesymlinks.service \
        obmc-vpnor-check-clearvolatile@.service \
        obmc-vpnor-enable-clearvolatile@.service \
        "

# To handle warm reboot scenarios, the checking and clearing of
# the volatile section must occur in both the chassis and host
# targets

# Host target installation - only clear if going through
# full host start target
ENABLE_CLEAR_VOLATILE_TMPL = "obmc-vpnor-enable-clearvolatile@.service"
HOST_START_TGTFMT = "obmc-host-start@{0}.target"
ENABLE_CLEAR_VOLATILE_INSTFMT = "obmc-vpnor-enable-clearvolatile@{0}.service"
ENABLE_CLEAR_VOLATILE_START_FMT = "../${ENABLE_CLEAR_VOLATILE_TMPL}:${HOST_START_TGTFMT}.requires/${ENABLE_CLEAR_VOLATILE_INSTFMT}"

CHECK_CLEAR_VOLATILE_TMPL = "obmc-vpnor-check-clearvolatile@.service"
HOST_STARTMIN_TGTFMT = "obmc-host-startmin@{0}.target"
CHECK_CLEAR_VOLATILE_INSTFMT = "obmc-vpnor-check-clearvolatile@{0}.service"
CHECK_CLEAR_VOLATILE_START_FMT = "../${CHECK_CLEAR_VOLATILE_TMPL}:${HOST_STARTMIN_TGTFMT}.requires/${CHECK_CLEAR_VOLATILE_INSTFMT}"

SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'ENABLE_CLEAR_VOLATILE_START_FMT', 'OBMC_HOST_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'CHECK_CLEAR_VOLATILE_START_FMT', 'OBMC_HOST_INSTANCES')}"

# Chassis target installation - always enable and clear in chassis
# power on
CHASSIS_PON_TGTFMT = "obmc-chassis-poweron@{0}.target"
ENABLE_CLEAR_VOLATILE_PON_FMT = "../${ENABLE_CLEAR_VOLATILE_TMPL}:${CHASSIS_PON_TGTFMT}.requires/${ENABLE_CLEAR_VOLATILE_INSTFMT}"

CHECK_CLEAR_VOLATILE_PON_FMT = "../${CHECK_CLEAR_VOLATILE_TMPL}:${CHASSIS_PON_TGTFMT}.requires/${CHECK_CLEAR_VOLATILE_INSTFMT}"

SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'ENABLE_CLEAR_VOLATILE_PON_FMT', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'CHECK_CLEAR_VOLATILE_PON_FMT', 'OBMC_CHASSIS_INSTANCES')}"
