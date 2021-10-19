PACKAGECONFIG:append = " pldm"

SYSTEMD_SERVICE:${PN} += " \
    openpower-process-host-firmware.service \
    openpower-update-bios-attr-table.service \
"
