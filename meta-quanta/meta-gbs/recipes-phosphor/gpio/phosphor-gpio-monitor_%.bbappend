inherit obmc-phosphor-systemd

FILESEXTRAPATHS:prepend:gbs := "${THISDIR}/${PN}:"

SRC_URI:append:gbs = " file://phosphor-gpio-presence@.service.replace"

TMPL_PRESENT = "phosphor-gpio-presence@.service"
INSTFMT_PRESENT = "phosphor-gpio-presence@{0}.service"
PRESENT_TGT = "multi-user.target"
FMT_PRESENT = "../${TMPL_PRESENT}:${PRESENT_TGT}.requires/${INSTFMT_PRESENT}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:gbs = " obmc/gpio/gpios-0.conf"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:gbs = " obmc/gpio/gpios-1.conf"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:gbs = " obmc/gpio/gpios-2.conf"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:gbs = " obmc/gpio/gpios-3.conf"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:gbs = " obmc/gpio/gpios-4.conf"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:gbs = " obmc/gpio/gpios-5.conf"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:gbs = " obmc/gpio/gpios-6.conf"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:gbs = " obmc/gpio/gpios-7.conf"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:gbs = " obmc/gpio/gpios-8.conf"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:gbs = " obmc/gpio/gpios-9.conf"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:gbs = " obmc/gpio/gpios-10.conf"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:gbs = " obmc/gpio/gpios-11.conf"
GBS_OBMC_PRESENT_INSTANCES = "0 1 2 3 4 5 6 7 8 9 10 11"

SYSTEMD_LINK:${PN}-presence:append:gbs = " ${@compose_list(d, 'FMT_PRESENT', 'GBS_OBMC_PRESENT_INSTANCES')}"

GBS_PRESENT_ENV_FMT = "obmc/gpio/gpios-{0}.conf"

SYSTEMD_ENVIRONMENT_FILE:${PN}-presence:gbs = " ${@compose_list(d, 'GBS_PRESENT_ENV_FMT', 'GBS_OBMC_PRESENT_INSTANCES')}"

do_install:append:gbs() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/phosphor-gpio-presence@.service.replace ${D}${systemd_system_unitdir}/phosphor-gpio-presence@.service
}
