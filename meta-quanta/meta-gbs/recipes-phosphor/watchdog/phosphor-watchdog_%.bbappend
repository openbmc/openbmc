FILESEXTRAPATHS:prepend:gbs := "${THISDIR}/${PN}:"

SYSTEMD_ENVIRONMENT_FILE:${PN}:append:gbs = " obmc/watchdog/host0"
OBMC_HOST_WATCHDOG_INSTANCES_A = "host0"
WATCHDOG_FMT_A = "phosphor-watchdog@{0}.service"
SYSTEMD_OVERRIDE:${PN}:remove:gbs = "poweron.conf:phosphor-watchdog@poweron.service.d/poweron.conf"
SYSTEMD_LINK:${PN}:remove:gbs = "${@compose_list(d, 'ENABLE_WATCHDOG_FMT', 'OBMC_HOST_INSTANCES')}"
SYSTEMD_LINK:${PN}:remove:gbs = "${@compose_list(d, 'WATCHDOG_FMT', 'OBMC_HOST_WATCHDOG_INSTANCES', 'OBMC_HOST_INSTANCES')}"
SYSTEMD_SERVICE:${PN}:gbs = " ${@compose_list(d, 'WATCHDOG_FMT_A', 'OBMC_HOST_WATCHDOG_INSTANCES_A')} \
                              phosphor-host-watchdog-reset.service \
                              phosphor-host-watchdog-poweroff.service \
                              phosphor-host-watchdog-powercycle.service \
                            "
