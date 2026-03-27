FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"
OBMC_CONSOLE_HOST_TTY = "ttyS3"

SRC_URI:append = " \
    file://uart-routing.conf \
"

do_install:append() {
    # Override the obmc-console service to set UART routing
    install -d ${D}${systemd_system_unitdir}/obmc-console@.service.d
    install -m 0644 ${UNPACKDIR}/uart-routing.conf ${D}${systemd_system_unitdir}/obmc-console@.service.d/uart-routing.conf
}
