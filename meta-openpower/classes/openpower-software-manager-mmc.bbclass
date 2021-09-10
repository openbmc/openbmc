PACKAGECONFIG:append = " mmc_layout"

SYSTEMD_SERVICE:${PN} += " \
    obmc-flash-bios-init.service \
    obmc-flash-bios-patch.service \
    openpower-process-host-firmware.service \
    openpower-update-bios-attr-table.service \
"
