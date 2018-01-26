FILESEXTRAPATHS_append := "${THISDIR}/${PN}:"

# Package configuration
FAN_PACKAGES += " \
        phosphor-cooling-type \
"

PACKAGECONFIG += "cooling-type"

TMPL_COOLING = "phosphor-cooling-type@.service"
INSTFMT_COOLING = "phosphor-cooling-type@{0}.service"
COOLING_TGT = "${SYSTEMD_DEFAULT_TARGET}"
FMT_COOLING = "../${TMPL_COOLING}:${COOLING_TGT}.requires/${INSTFMT_COOLING}"

FILES_phosphor-cooling-type = "${sbindir}/phosphor-cooling-type"
SYSTEMD_SERVICE_phosphor-cooling-type += "${TMPL_COOLING}"
SYSTEMD_LINK_phosphor-cooling-type += "${@compose_list(d, 'FMT_COOLING', 'OBMC_CHASSIS_INSTANCES')}"

COOLING_ENV_FMT = "obmc/phosphor-fan/phosphor-cooling-type-{0}.conf"

SYSTEMD_ENVIRONMENT_FILE_phosphor-cooling-type += "${@compose_list(d, 'COOLING_ENV_FMT', 'OBMC_CHASSIS_INSTANCES')}"

