FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd

SRC_URI:append = " \
    file://control-fio-led \
"

SYSTEMD_SERVICE:${PN}:append = " \
    disable-fio-led@.service \
    enable-fio-led@.service \
"

do_install:append() {
    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/control-fio-led ${D}${bindir}/control-fio-led
}
