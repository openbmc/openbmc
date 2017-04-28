FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

COOLING_ENV_FMT = "phosphor-cooling-type-{0}.conf"

SYSTEMD_ENVIRONMENT_FILE_phosphor-chassis-cooling-type += "${@compose_list(d, 'COOLING_ENV_FMT', 'OBMC_CHASSIS_INSTANCES')}"
