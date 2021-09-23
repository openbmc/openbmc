FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
             file://firmware_update.sh \
           "

PACKAGECONFIG[flash_bios] = "-Dhost-bios-upgrade=enabled, -Dhost-bios-upgrade=disabled"

PACKAGECONFIG:append = " flash_bios"

SYSTEMD_SERVICE:${PN}:updater += "${@bb.utils.contains('PACKAGECONFIG', 'flash_bios', 'obmc-flash-host-bios@.service', '', d)}"

RDEPENDS:${PN} += "bash"

do_install:append() {
    install -d ${D}/usr/sbin
    install -m 0755 ${WORKDIR}/firmware_update.sh ${D}/usr/sbin/firmware_update.sh
}
