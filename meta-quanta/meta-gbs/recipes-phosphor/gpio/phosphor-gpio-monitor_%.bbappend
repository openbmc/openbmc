inherit obmc-phosphor-utils
inherit systemd

FILESEXTRAPATHS:prepend:gbs := "${THISDIR}/${PN}:"

SRC_URI:append:gbs = " \
    file://phosphor-gpio-presence@.service.replace \
    file://obmc \
    "

SYSTEMD_SERVICE_FMT:gbs = "phosphor-gpio-presence@{0}.service"
SYSTEMD_SERVICE:${PN}-presence:append:gbs = " ${@compose_list(d, 'SYSTEMD_SERVICE_FMT', 'GBS_OBMC_PRESENT_INSTANCES')}"
GBS_OBMC_PRESENT_INSTANCES = "0 1 2 3 4 5 6 7 8 9 10 11"

FILES:${PN}-presence:append:gbs = " ${systemd_system_unitdir}/phosphor-gpio-presence@.service ${sysconfdir}/default/obmc/gpio/*.conf"

do_install:append:gbs() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/phosphor-gpio-presence@.service.replace ${D}${systemd_system_unitdir}/phosphor-gpio-presence@.service

    for i in ${GBS_OBMC_PRESENT_INSTANCES}; do
        install -d ${D}${sysconfdir}/default/obmc/gpio/
        install -m 0644 ${UNPACKDIR}/obmc/gpio/gpios-$i.conf ${D}${sysconfdir}/default/obmc/gpio/
    done
}
