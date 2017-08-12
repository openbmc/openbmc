inherit obmc-phosphor-license

SYSTEMD_SERVICE_${PN} = "prepare-overlay.service mount-machine-id.service"

inherit obmc-phosphor-systemd
