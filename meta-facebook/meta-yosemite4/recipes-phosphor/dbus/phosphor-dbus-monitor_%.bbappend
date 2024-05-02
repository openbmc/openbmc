FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd

SRC_URI:append = " \
    file://switch-nic-main-or-aux-mode \
"

SYSTEMD_SERVICE:${PN}:append = " \
    switch-nic-to-aux-mode@.service \
    switch-nic-to-main-mode@.service \
"

do_install:append() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/switch-nic-main-or-aux-mode ${D}${bindir}/switch-nic-main-or-aux-mode
}
