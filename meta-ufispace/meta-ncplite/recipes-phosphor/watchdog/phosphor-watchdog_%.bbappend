FILESEXTRAPATHS:prepend:ncplite := "${THISDIR}/${PN}:"

# Remove the override to keep service running after DC cycle
SYSTEMD_OVERRIDE:${PN}:remove:ncplite = "poweron.conf:phosphor-watchdog@poweron.service.d/poweron.conf"
SYSTEMD_SERVICE:${PN}:ncplite = " phosphor-watchdog.service \
                                phosphor-watchdog-host-poweroff.service \
                                phosphor-watchdog-host-cycle.service\
                                phosphor-watchdog-host-reset.service\
                                "
