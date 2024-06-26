FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://bios-update.sh"

PACKAGECONFIG:append = " flash_bios"
RDEPENDS:${PN} += "bash flashrom phosphor-ipmi-ipmb"

do_install:append() {
    install -d ${D}/${sbindir}
    install -m 0755 ${UNPACKDIR}/bios-update.sh ${D}/${sbindir}/
}
