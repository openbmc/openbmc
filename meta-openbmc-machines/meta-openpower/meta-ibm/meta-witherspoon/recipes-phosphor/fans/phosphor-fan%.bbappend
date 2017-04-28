FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG += "cooling-type"

# Package configuration
FAN_PACKAGES += " \
        phosphor-cooling-type \
"
RDEPENDS_phosphor-cooling-type += "libevdev"

COOLING_TMPL = "phosphor-cooling-type@.service"
COOLING_INSTFMT = "phosphor-cooling-type@{0}.service"
COOLING_TGT = "${SYSTEMD_DEFAULT_TARGET}"
COOLING_FMT = "../${COOLING_TMPL}:${COOLING_TGT}.requires/${COOLING_INSTFMT}"

FILES_phosphor-cooling-type = "${sbindir}/phosphor-cooling-type"
SYSTEMD_SERVICE_phosphor-cooling-type += "${COOLING_TMPL}"
SYSTEMD_LINK_phosphor-cooling-type += "${@compose_list(d, 'COOLING_FMT', 'OBMC_CHASSIS_INSTANCES')}"

COOLING_ENV_FMT = "obmc/phosphor-fan/phosphor-cooling-type-{0}.conf"

SYSTEMD_ENVIRONMENT_FILE_phosphor-chassis-cooling-type += "${@compose_list(d, 'COOLING_ENV_FMT', 'OBMC_CHASSIS_INSTANCES')}"

