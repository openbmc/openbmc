FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG[flash_bios] = "-Dhost-bios-upgrade=enabled, -Dhost-bios-upgrade=disabled"

PACKAGECONFIG:append_ = " flash_bios"

SYSTEMD_SERVICE:${PN}-updater += "${@bb.utils.contains('PACKAGECONFIG', 'flash_bios', 'obmc-flash-host-bios@.service', '', d)}"
