FILESEXTRAPATHS:append:ncplite := "${THISDIR}/${PN}:"

NCPLITE_OBMC_GPIO_INSTANCES = "0 1 2 3 4 5 6 7 8 9 10 11 12 13 14"
NCPLITE_OBMC_GPIO_ENV_FMT = "obmc/gpio/gpio-{0}.conf"

TMPL = "phosphor-gpio-presence@.service"
INSTFMT = "phosphor-gpio-presence@{0}.service"
TGT = "multi-user.target"
FMT = "../${TMPL}:${TGT}.requires/${INSTFMT}"

SYSTEMD_LINK:${PN}-presence:append:ncplite = " ${@compose_list(d, 'FMT', 'NCPLITE_OBMC_GPIO_INSTANCES')}"
SYSTEMD_ENVIRONMENT_FILE:${PN}-presence:append:ncplite = " ${@compose_list(d, 'NCPLITE_OBMC_GPIO_ENV_FMT', 'NCPLITE_OBMC_GPIO_INSTANCES')}"
