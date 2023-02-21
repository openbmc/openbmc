# Nuvoton implement PFR in fTPM, so we don't need check U-Boot again.

# move mirror uboot service to disable
SYSTEMD_SERVICE:phosphor-software-manager-updater-mmc:remove:nuvoton = "obmc-flash-mmc-mirroruboot.service"
SOFTWARE_MGR_PACKAGES:append:nuvoton = " ${PN}-disabled"
SYSTEMD_SERVICE:${PN}-disabled:nuvoton = "obmc-flash-mmc-mirroruboot.service"
SYSTEMD_AUTO_ENABLE:${PN}-disabled:nuvoton = "disable"
