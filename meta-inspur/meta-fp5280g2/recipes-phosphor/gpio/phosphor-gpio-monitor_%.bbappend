FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

TMPL_POWERSUPPLY = "phosphor-gpio-presence@.service"
INSTFMT_POWERSUPPLY = "phosphor-gpio-presence@{0}.service"
POWERSUPPLY_TGT = "multi-user.target"
FMT_POWERSUPPLY = "../${TMPL_POWERSUPPLY}:${POWERSUPPLY_TGT}.requires/${INSTFMT_POWERSUPPLY}"

SYSTEMD_LINK_${PN}-presence_append_fp5280g2 = " ${@compose_list(d, 'FMT_POWERSUPPLY', 'OBMC_POWER_SUPPLY_INSTANCES')}"

POWERSUPPLY_ENV_FMT  = "obmc/gpio/phosphor-power-supply-{0}.conf"

SYSTEMD_ENVIRONMENT_FILE_${PN}-presence_append_fp5280g2 = " ${@compose_list(d, 'POWERSUPPLY_ENV_FMT', 'OBMC_POWER_SUPPLY_INSTANCES')}"
