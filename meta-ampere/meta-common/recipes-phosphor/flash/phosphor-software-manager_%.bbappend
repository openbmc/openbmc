FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
             file://firmware_update.sh \
           "

PACKAGECONFIG:append = " flash_bios static-dual-image"

RDEPENDS:${PN} += "bash"

do_install:append() {
    install -d ${D}/usr/sbin
    install -m 0755 ${UNPACKDIR}/firmware_update.sh ${D}/usr/sbin/firmware_update.sh
}
