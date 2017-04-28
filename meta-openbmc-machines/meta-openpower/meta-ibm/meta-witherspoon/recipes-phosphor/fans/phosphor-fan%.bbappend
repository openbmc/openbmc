FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

COOLING_TGT = "${SYSTEMD_DEFAULT_TARGET}"
COOLING_FMT = "../${COOLING_TMPL}:${COOLING_TGT}.requires/${COOLING_INSTFMT}"
SYSTEMD_LINK_phosphor-cooling-type += "${@compose_list(d, 'COOLING_FMT', 'OBMC_CHASSIS_INSTANCES')}"
COOLING_ENV_FMT = "phosphor-cooling-type-{0}.conf"
SYSTEMD_ENVIRONMENT_FILE_phosphor-cooling-type += "${@compose_list(d, 'COOLING_ENV_FMT', 'OBMC_CHASSIS_INSTANCES')}"
