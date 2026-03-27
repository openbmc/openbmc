FILESEXTRAPATHS:prepend:mori := "${THISDIR}/${PN}:"

SRC_URI:append:mori = " \
    file://mori-overrides.conf \
    file://host_console_uart_config.service \
    file://mori_uart_mux_ctrl.sh \
    file://server.ttyS1.conf \
    file://server.ttyS3.conf \
"

SYSTEMD_SERVICE:${PN}:append:mori = " \
    ${BPN}@.service \
    host_console_uart_config.service \
"

do_install:append:mori() {
    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/mori_uart_mux_ctrl.sh \
        ${D}${libexecdir}/${PN}/mori_uart_mux_ctrl.sh
    install -m 0644 ${UNPACKDIR}/host_console_uart_config.service \
        ${D}${systemd_unitdir}/system
    install -d ${D}${systemd_unitdir}/system/obmc-console@.service.d
    install -m 0644 ${UNPACKDIR}/mori-overrides.conf \
        ${D}${systemd_unitdir}/system/obmc-console@.service.d/mori-overrides.conf
}

RDEPENDS:${PN}:append:mori = " bash"

pkg_postinst:${PN}:append:mori () {
    systemctl --root=$D enable obmc-console@ttyS1.service
    systemctl --root=$D enable obmc-console@ttyS3.service
}

OBMC_CONSOLE_TTYS:mori = "ttyS1 ttyS3"
