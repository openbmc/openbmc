FILESEXTRAPATHS:prepend:= "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd

SRC_URI:append = " \
                  file://${BPN}.service \
                 "

PACKAGECONFIG:append = " handle-missing-object-paths "

do_install:append() {
    install -m 644 ${UNPACKDIR}/${BPN}.service ${D}${systemd_system_unitdir}
}
