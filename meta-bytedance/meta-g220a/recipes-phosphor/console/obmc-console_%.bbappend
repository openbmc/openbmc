FILESEXTRAPATHS:append := ":${THISDIR}/${PN}"
OBMC_CONSOLE_HOST_TTY = "ttyS2"
SRC_URI += "\
    file://uart-routing.conf \
    "
inherit systemd

SYSTEMD_SERVICE:${PN} += " \
        ${PN}@${OBMC_CONSOLE_HOST_TTY}.service \
        "

do_install:append() {
        rm -rf ${D}${nonarch_base_libdir}/udev/rules.d/80-obmc-console-uart.rules
        install -d ${D}${systemd_system_unitdir}/obmc-console@.service.d
        install -m 0644 ${UNPACKDIR}/uart-routing.conf ${D}${systemd_system_unitdir}/obmc-console@.service.d/uart-routing.conf
}

