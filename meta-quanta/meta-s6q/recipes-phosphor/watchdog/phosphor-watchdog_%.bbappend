FILESEXTRAPATHS:prepend:s6q := "${THISDIR}/${PN}:"

SYSTEMD_OVERRIDE:${PN}:remove:s6q = "poweron.conf:phosphor-watchdog@poweron.service.d/poweron.conf"
SYSTEMD_SERVICE:${PN}:s6q = " phosphor-watchdog.service \
                              phosphor-watchdog-host-reset.service \
                              phosphor-watchdog-host-poweroff.service \
                              phosphor-watchdog-host-powercycle.service \
                            "
