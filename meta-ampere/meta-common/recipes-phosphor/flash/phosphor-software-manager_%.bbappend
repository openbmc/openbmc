FILESEXTRAPATHS_prepend_mtjade := "${THISDIR}/${PN}:"

#PACKAGECONFIG[flash_bios] = "-Dhost-bios-upgrade=enabled, -Dhost-bios-upgrade=disabled"

PACKAGECONFIG_append_mtjade = " flash_bios"

SYSTEMD_SERVICE_${PN}-updater += "${@bb.utils.contains('PACKAGECONFIG', 'flash_bios', 'obmc-flash-host-bios@.service', '', d)}"

SRC_URI += " \
            file://0001-Implement-Software.Extended-Version.patch \
            file://0002-Add-other-image-update-support.patch \
           "
