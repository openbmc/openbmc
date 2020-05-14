FILESEXTRAPATHS_prepend_gbs := "${THISDIR}/files:"
SRC_URI_append_gbs = " file://80-gbs-nuvoton-sol.rules"

do_install_append_gbs() {
        install -m 0755 -d ${D}${sysconfdir}/${BPN}
        rm -f ${D}${sysconfdir}/${BPN}/server.ttyVUART0.conf
        install -m 0644 ${WORKDIR}/${BPN}.conf ${D}${sysconfdir}/
        ln -sr ${D}${sysconfdir}/${BPN}.conf ${D}${sysconfdir}/${BPN}/server.ttyS1.conf

        install -d ${D}/lib/udev/rules.d
        rm -f ${D}/lib/udev/rules.d/80-obmc-console-uart.rules
        install -m 0644 ${WORKDIR}/80-gbs-nuvoton-sol.rules ${D}/lib/udev/rules.d
}
