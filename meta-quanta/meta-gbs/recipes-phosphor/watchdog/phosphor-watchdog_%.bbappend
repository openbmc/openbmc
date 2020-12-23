FILESEXTRAPATHS_prepend_gbs := "${THISDIR}/${PN}:"

SRC_URI_append_gbs = " file://0001-Customize-phosphor-watchdog-for-Intel-platforms.patch"

# Remove the override to keep service running after DC cycle
SYSTEMD_OVERRIDE_${PN}_remove_gbs = "poweron.conf:phosphor-watchdog@poweron.service.d/poweron.conf"
SYSTEMD_SERVICE_${PN}_gbs = "phosphor-watchdog.service"
