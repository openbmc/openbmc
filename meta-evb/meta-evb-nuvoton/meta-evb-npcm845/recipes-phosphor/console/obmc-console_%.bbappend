FILESEXTRAPATHS:prepend:evb-npcm845 := "${THISDIR}/${PN}:"

SRC_URI:append:evb-npcm845 = " file://80-evb-npcm845-sol.rules"

do_install:append:evb-npcm845() {
        install -m 0755 -d ${D}${sysconfdir}/${BPN}
        rm -f ${D}${sysconfdir}/${BPN}/server.ttyVUART0.conf
        install -m 0644 ${WORKDIR}/${BPN}.conf ${D}${sysconfdir}/
        ln -sr ${D}${sysconfdir}/${BPN}.conf ${D}${sysconfdir}/${BPN}/server.ttyS1.conf

        install -d ${D}/lib/udev/rules.d
        rm -f ${D}/lib/udev/rules.d/80-obmc-console-uart.rules
        install -m 0644 ${WORKDIR}/80-evb-npcm845-sol.rules ${D}/lib/udev/rules.d
}
