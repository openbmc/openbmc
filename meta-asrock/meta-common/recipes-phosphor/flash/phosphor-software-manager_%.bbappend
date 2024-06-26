FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://bios-update.sh"

PACKAGECONFIG:append = " flash_bios"
RDEPENDS:${PN} += "bash libgpiod"

do_install:append() {
    install -d ${D}/${sbindir}
    install -m 0755 ${UNPACKDIR}/bios-update.sh ${D}/${sbindir}/
    if [ -e ${UNPACKDIR}/bios-update ]; then
        install -d ${D}${sysconfdir}/default
        install -m 0644 ${UNPACKDIR}/bios-update ${D}${sysconfdir}/default
    fi
}
