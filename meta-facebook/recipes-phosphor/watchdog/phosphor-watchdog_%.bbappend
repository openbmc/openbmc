FILESEXTRAPATHS_append := ":${THISDIR}/${PN}"

# Remove the override to keep service running after DC cycle
SYSTEMD_OVERRIDE_${PN}_remove = "poweron.conf:phosphor-watchdog@poweron.service.d/poweron.conf"
SYSTEMD_SERVICE_${PN} = "phosphor-watchdog.service phosphor-watchdog-host-poweroff.service phosphor-watchdog-host-reset.service phosphor-watchdog-host-cycle.service"
