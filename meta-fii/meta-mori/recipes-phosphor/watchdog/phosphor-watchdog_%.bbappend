FILESEXTRAPATHS:prepend:mori := "${THISDIR}/${PN}:"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:mori = " obmc/watchdog/host0"

SYSTEMD_LINK:${PN}:remove:mori = "${@compose_list(d, 'ENABLE_WATCHDOG_FMT', 'OBMC_HOST_INSTANCES')}"
SYSTEMD_LINK:${PN}:remove:mori = "${@compose_list(d, 'WATCHDOG_FMT', 'OBMC_HOST_WATCHDOG_INSTANCES', 'OBMC_HOST_INSTANCES')}"
# Remove the override to keep service running after DC cycle
SYSTEMD_OVERRIDE_${PN}:remove:mori = "poweron.conf:phosphor-watchdog@poweron.service.d/poweron.conf"
SYSTEMD_SERVICE:${PN}:mori = "  phosphor-watchdog.service \
                                "
