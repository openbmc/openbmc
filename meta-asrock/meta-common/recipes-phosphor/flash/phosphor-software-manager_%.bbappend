FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://bios-update.sh"

PACKAGECONFIG:append = " flash_bios"
RDEPENDS:${PN} += "bash libgpiod"

do_install:append() {
    install -d ${D}/${sbindir}
    install -m 0755 ${WORKDIR}/bios-update.sh ${D}/${sbindir}/
    if [ -e ${WORKDIR}/bios-update ]; then
        install -d ${D}${sysconfdir}/default
        install -m 0644 ${WORKDIR}/bios-update ${D}${sysconfdir}/default
    fi
}
