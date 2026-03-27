FILESEXTRAPATHS:prepend:kudo := "${THISDIR}/${PN}:"
RDEPENDS:${PN}:append:kudo = " bash"

OBMC_CONSOLE_TTYS = "ttyS1 ttyS3"

SRC_URI:append:kudo = " file://uart-routing.conf \
                        file://kudo_uart_mux_ctrl.sh \
                        file://server.ttyS1.conf \
                        file://server.ttyS3.conf \
                      "

SYSTEMD_SERVICE:${PN}:append:kudo = " \
        ${BPN}@.service \
        "

do_install:append() {
    install -d ${D}${systemd_unitdir}/system/obmc-console@.service.d
    install -m 0644 ${UNPACKDIR}/uart-routing.conf ${D}${systemd_unitdir}/system/obmc-console@.service.d/uart-routing.conf

    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/kudo_uart_mux_ctrl.sh ${D}${libexecdir}/${PN}/kudo_uart_mux_ctrl.sh
}

pkg_postinst:${PN}:append () {
    systemctl --root=$D enable obmc-console@ttyS1.service
    systemctl --root=$D enable obmc-console@ttyS3.service
}

