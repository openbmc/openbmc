FILESEXTRAPATHS:prepend:buv-runbmc := "${THISDIR}/${PN}:"

SRC_URI:append:buv-runbmc = " file://80-buv-runbmc-sol.rules"
OBMC_CONSOLE_HOST_TTY:buv-runbmc = "ttyS2"

do_install:append:buv-runbmc() {
        install -d ${D}/lib/udev/rules.d
        rm -f ${D}/lib/udev/rules.d/80-obmc-console-uart.rules
        install -m 0644 ${WORKDIR}/80-buv-runbmc-sol.rules ${D}/lib/udev/rules.d
}
