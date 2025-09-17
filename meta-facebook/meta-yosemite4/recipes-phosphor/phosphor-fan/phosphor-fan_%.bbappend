EXTRA_OEMESON = "-Dskip-power-checking=enabled"

PACKAGECONFIG:remove = "sensor-monitor"
SYSTEMD_SERVICE:${PN}-sensor-monitor:remove = "sensor-monitor.service"
SYSTEMD_LINK:${PN}-sensor-monitor = ""
