FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
RDEPENDS:${PN}:append = " bash"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-dbus-service

SRC_URI:append = " \
    file://plat-80-obmc-console-uart.rules \
    file://server.ttyUSB1.conf \
    file://server.ttyUSB5.conf \
    file://server.VSER0.conf \
    file://server.VSER1.conf \
    file://server.VSER2.conf \
    file://server.VSER3.conf \
    file://server.VSER4.conf \
    file://server.VSER5.conf \
    file://server.VSER6.conf \
    file://server.VSER7.conf \
    file://server.VSER8.conf \
    file://server.VSER9.conf \
    file://server.VSER10.conf \
    file://server.VSER11.conf \
    file://server.VSER12.conf \
    file://server.VSER13.conf \
    file://server.VSER14.conf \
    file://server.VSER15.conf \
    file://server.VSER16.conf \
    file://server.VSER17.conf \
    file://server.VSER18.conf \
    file://server.VSER19.conf \
    file://server.VSER20.conf \
    file://server.VSER21.conf \
    file://server.VSER22.conf \
    file://server.VSER23.conf \
    file://server.VSER24.conf \
    file://server.VSER25.conf \
    file://server.VSER26.conf \
    file://server.VSER27.conf \
    file://server.VSER28.conf \
    file://server.VSER29.conf \
    file://server.VSER30.conf \
    file://server.VSER31.conf \
    file://use-service-VSER.conf \
    "

OBMC_CONSOLE_TTYS:append = " \
    ttyUSB1 ttyUSB5 \
    VSER0 VSER1 VSER2 VSER3 VSER4 VSER5 VSER6 VSER7 \
    VSER8 VSER9 VSER10 VSER11 VSER12 VSER13 VSER14 VSER15 \
    VSER16 VSER17 VSER18 VSER19 VSER20 VSER21 VSER22 VSER23 \
    VSER24 VSER25 VSER26 VSER27 VSER28 VSER29 VSER30 VSER31 \
    "

SYSTEMD_OVERRIDE:${PN}:append = " \
    use-service-VSER.conf:obmc-console@VSER0.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER1.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER2.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER3.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER4.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER5.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER6.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER7.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER8.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER9.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER10.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER11.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER12.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER13.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER14.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER15.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER16.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER17.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER18.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER19.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER20.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER21.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER22.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER23.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER24.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER25.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER26.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER27.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER28.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER29.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER30.service.d/use-service-VSER.conf \
    use-service-VSER.conf:obmc-console@VSER31.service.d/use-service-VSER.conf \
    "

do_install:append() {
        install -d ${D}${base_libdir}/udev/rules.d/
        install -m 0644 ${UNPACKDIR}/plat-80-obmc-console-uart.rules ${D}${base_libdir}/udev/rules.d/80-obmc-console-uart.rules
}
