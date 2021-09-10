FILESEXTRAPATHS:append := ":${THISDIR}/${PN}"
OBMC_CONSOLE_HOST_TTY = "ttyS2"
SRC_URI += "file://obmc-console@.service \
           "
inherit obmc-phosphor-systemd

SYSTEMD_SERVICE:${PN} += " \
        ${PN}@${OBMC_CONSOLE_HOST_TTY}.service \
        "

do_install:append() {
        rm -rf ${D}${nonarch_base_libdir}/udev/rules.d/80-obmc-console-uart.rules
        install -m 0644 ${WORKDIR}/${PN}@.service ${D}${systemd_system_unitdir}
}
