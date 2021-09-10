FILESEXTRAPATHS:prepend:kudo := "${THISDIR}/${PN}:"

# Remove the override to keep service running after DC cycle
SYSTEMD_OVERRIDE:${PN}:remove:kudo = "poweron.conf:phosphor-watchdog@poweron.service.d/poweron.conf"
SYSTEMD_SERVICE:${PN}:kudo = "  phosphor-watchdog.service \
                                phosphor-watchdog-host-poweroff.service \
                                phosphor-watchdog-host-cycle.service\
                                phosphor-watchdog-host-reset.service\
                                "
