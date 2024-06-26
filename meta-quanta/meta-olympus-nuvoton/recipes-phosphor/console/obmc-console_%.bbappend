FILESEXTRAPATHS:prepend:olympus-nuvoton := "${THISDIR}/${PN}:"

SRC_URI:append:olympus-nuvoton = " file://80-olympus-nuvoton-sol.rules"

do_install:append:olympus-nuvoton() {
        install -m 0755 -d ${D}${sysconfdir}/${BPN}
        rm -f ${D}${sysconfdir}/${BPN}/server.ttyVUART0.conf
        install -m 0644 ${UNPACKDIR}/${BPN}.conf ${D}${sysconfdir}/
        ln -sr ${D}${sysconfdir}/${BPN}.conf ${D}${sysconfdir}/${BPN}/server.ttyS2.conf

        install -d ${D}/${nonarch_base_libdir}/udev/rules.d
        rm -f ${D}/${nonarch_base_libdir}/udev/rules.d/80-obmc-console-uart.rules
        install -m 0644 ${UNPACKDIR}/80-olympus-nuvoton-sol.rules ${D}/${nonarch_base_libdir}/udev/rules.d
}
