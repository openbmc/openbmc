PACKAGECONFIG_append = " virtual_pnor"

SYSTEMD_SERVICE_${PN} += " \
        obmc-vpnor-updatesymlinks.service \
        "
