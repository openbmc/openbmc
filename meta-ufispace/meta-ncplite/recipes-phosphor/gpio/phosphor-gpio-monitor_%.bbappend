inherit obmc-phosphor-utils
inherit systemd

SRC_URI:append:ncplite = " \
    file://phosphor-gpio-presence@.service \
    file://obmc/gpio/gpio-0.conf \
    file://obmc/gpio/gpio-1.conf \
    file://obmc/gpio/gpio-2.conf \
    file://obmc/gpio/gpio-3.conf \
    file://obmc/gpio/gpio-4.conf \
    file://obmc/gpio/gpio-5.conf \
    file://obmc/gpio/gpio-6.conf \
    file://obmc/gpio/gpio-7.conf \
    file://obmc/gpio/gpio-8.conf \
    file://obmc/gpio/gpio-9.conf \
    file://obmc/gpio/gpio-10.conf \
    file://obmc/gpio/gpio-11.conf \
    file://obmc/gpio/gpio-12.conf \
    file://obmc/gpio/gpio-13.conf \
    file://obmc/gpio/gpio-14.conf \
    "

FILESEXTRAPATHS:append:ncplite := "${THISDIR}/${PN}:"

NCPLITE_OBMC_GPIO_INSTANCES = "0 1 2 3 4 5 6 7 8 9 10 11 12 13 14"

SYSTEMD_SERVICE_FMT:ncplite = "phosphor-gpio-presence@{0}.service"
SYSTEMD_SERVICE:${PN}-presence:append:ncplite = " ${@compose_list(d, 'SYSTEMD_SERVICE_FMT', 'NCPLITE_OBMC_GPIO_INSTANCES')}"

FILES:${PN}-presence:append:ncplite = " \
    ${systemd_system_unitdir}/phosphor-gpio-presence@.service \
    ${sysconfdir}/default/obmc/gpio/*.conf \
    "

do_install:append:ncplite() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/phosphor-gpio-presence@.service \
        ${D}${systemd_system_unitdir}/phosphor-gpio-presence@.service

    for i in ${NCPLITE_OBMC_GPIO_INSTANCES}; do
        install -d ${D}${sysconfdir}/default/obmc/gpio/
        install -m 0644 ${UNPACKDIR}/obmc/gpio/gpio-$i.conf \
            ${D}${sysconfdir}/default/obmc/gpio/
    done
}
