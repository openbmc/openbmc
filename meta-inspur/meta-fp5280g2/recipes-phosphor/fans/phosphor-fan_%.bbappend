FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

# Package configuration
FAN_PACKAGES += " \
        phosphor-cooling-type \
"

PACKAGECONFIG:append:fp5280g2 = " cooling-type"

TMPL_COOLING = "phosphor-cooling-type@.service"
INSTFMT_COOLING = "phosphor-cooling-type@{0}.service"
COOLING_TGT = "multi-user.target"
FMT_COOLING = "../${TMPL_COOLING}:${COOLING_TGT}.requires/${INSTFMT_COOLING}"

FILES:phosphor-cooling-type = "${bindir}/phosphor-cooling-type"
SYSTEMD_SERVICE:phosphor-cooling-type += "${TMPL_COOLING}"
SYSTEMD_LINK:phosphor-cooling-type += "${@compose_list(d, 'FMT_COOLING', 'OBMC_CHASSIS_INSTANCES')}"

COOLING_ENV_FMT = "obmc/phosphor-fan/phosphor-cooling-type-{0}.conf"

SYSTEMD_ENVIRONMENT_FILE:phosphor-cooling-type:append:fp5280g2 = " ${@compose_list(d, 'COOLING_ENV_FMT', 'OBMC_CHASSIS_INSTANCES')}"
