FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://bios-update"

PACKAGECONFIG:append = " flash_bios"
PACKAGECONFIG:append = " eepromdevice-software-update"
PACKAGECONFIG:append = " i2cvr-software-update"
RDEPENDS:${PN} += "bash"

do_install:append() {
    install -d ${D}/${sbindir}
    install -m 0755 ${UNPACKDIR}/bios-update ${D}/${sbindir}/
}
