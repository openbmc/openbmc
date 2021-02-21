FILESEXTRAPATHS_prepend_kudo := "${THISDIR}/${PN}:"

# Remove the override to keep service running after DC cycle
SYSTEMD_OVERRIDE_${PN}_remove_kudo = "poweron.conf:phosphor-watchdog@poweron.service.d/poweron.conf"
SYSTEMD_SERVICE_${PN}_kudo = "  phosphor-watchdog.service \
                                phosphor-watchdog-host-poweroff.service \
                                phosphor-watchdog-host-cycle.service\
                                phosphor-watchdog-host-reset.service\
                                "
