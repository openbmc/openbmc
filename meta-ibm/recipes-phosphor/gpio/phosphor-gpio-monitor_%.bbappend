inherit obmc-phosphor-utils
inherit systemd

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:ibm-ac-server = " \
    file://obmc/gpio/phosphor-power-supply-0.conf \
    file://obmc/gpio/phosphor-power-supply-1.conf \
    "

SYSTEMD_SERVICE_FMT:ibm-ac-server = "phosphor-gpio-presence@{0}.service"
SYSTEMD_SERVICE:${PN}-presence:append:ibm-ac-server = " ${@compose_list(d, 'SYSTEMD_SERVICE_FMT', 'OBMC_POWER_SUPPLY_INSTANCES')}"

POWERSUPPLY_ENV_FMT = "obmc/gpio/phosphor-power-supply-{0}.conf"

FILES:${PN}-presence:append:ibm-ac-server = " \
    ${sysconfdir}/default/obmc/gpio/phosphor-power-supply-*.conf \
    "

do_install:append:ibm-ac-server() {
    for i in ${OBMC_POWER_SUPPLY_INSTANCES}; do
        install -d ${D}${sysconfdir}/default/obmc/gpio/
        install -m 0644 ${UNPACKDIR}/obmc/gpio/phosphor-power-supply-$i.conf \
            ${D}${sysconfdir}/default/obmc/gpio/
    done
}
