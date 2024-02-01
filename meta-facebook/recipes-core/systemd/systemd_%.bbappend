FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://systemd-networkd-only-wait-for-one.conf \
"

do_install:append() {
    install -m 644 -D \
        ${WORKDIR}/systemd-networkd-only-wait-for-one.conf \
        ${D}${systemd_system_unitdir}/systemd-networkd-wait-online.service.d/systemd-networkd-only-wait-for-one.conf
}
