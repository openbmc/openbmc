PACKAGECONFIG_append = " mmc_layout"

SYSTEMD_SERVICE_${PN} += " \
    obmc-flash-bios-init.service \
    obmc-flash-bios-patch.service \
"
