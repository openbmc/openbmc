FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

TMPL_POWERSUPPLY = "phosphor-gpio-presence@.service"
INSTFMT_POWERSUPPLY = "phosphor-gpio-presence@{0}.service"
POWERSUPPLY_TGT = "multi-user.target"
FMT_POWERSUPPLY = "../${TMPL_POWERSUPPLY}:${POWERSUPPLY_TGT}.wants/${INSTFMT_POWERSUPPLY}"

SYSTEMD_LINK_${PN}-presence_append_ibm-ac-server = " ${@compose_list(d, 'FMT_POWERSUPPLY', 'OBMC_POWER_SUPPLY_INSTANCES')}"
SYSTEMD_LINK_${PN}-presence_append_mihawk = " ${@compose_list(d, 'FMT_POWERSUPPLY', 'OBMC_POWER_SUPPLY_INSTANCES')}"
SYSTEMD_LINK_${PN}-presence_append_rainier = " ${@compose_list(d, 'FMT_POWERSUPPLY', 'OBMC_POWER_SUPPLY_INSTANCES')}"

POWERSUPPLY_ENV_FMT  = "obmc/gpio/phosphor-power-supply-{0}.conf"

SYSTEMD_ENVIRONMENT_FILE_${PN}-presence_append_ibm-ac-server = " ${@compose_list(d, 'POWERSUPPLY_ENV_FMT', 'OBMC_POWER_SUPPLY_INSTANCES')}"
SYSTEMD_ENVIRONMENT_FILE_${PN}-presence_append_mihawk = " ${@compose_list(d, 'POWERSUPPLY_ENV_FMT', 'OBMC_POWER_SUPPLY_INSTANCES')}"
SYSTEMD_ENVIRONMENT_FILE_${PN}-presence_append_rainier = " ${@compose_list(d, 'POWERSUPPLY_ENV_FMT', 'OBMC_POWER_SUPPLY_INSTANCES')}"
