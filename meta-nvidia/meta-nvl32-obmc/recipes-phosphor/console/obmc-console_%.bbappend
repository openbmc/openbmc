FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"
OBMC_CONSOLE_HOST_TTY = "ttyS3"

SRC_URI:append = " \
    file://obmc-console@.service \
"

SYSTEMD_SERVICE:${PN} += "obmc-console@.service "
FILES:${PN}  += "${systemd_system_unitdir}/obmc-console@.service"

do_install:append() {
    # Override the obmc-console service to set UART routing
    install -m 0644 ${UNPACKDIR}/obmc-console@.service ${D}${systemd_system_unitdir}
}
