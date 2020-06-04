inherit obmc-phosphor-systemd

FILESEXTRAPATHS_prepend_gbs := "${THISDIR}/${PN}:"

SRC_URI_append_gbs = " file://phosphor-gpio-presence@.service.replace"

TMPL_SLIMSASCABLE = "phosphor-gpio-presence@.service"
INSTFMT_SLIMSASCABLE = "phosphor-gpio-presence@{0}.service"
SLIMSASCABLE_TGT = "multi-user.target"
FMT_SLIMSASCABLE = "../${TMPL_SLIMSASCABLE}:${SLIMSASCABLE_TGT}.requires/${INSTFMT_SLIMSASCABLE}"
SYSTEMD_ENVIRONMENT_FILE_${PN}_append_gbs +="obmc/gpio/slimsas-cable-0.conf"
SYSTEMD_ENVIRONMENT_FILE_${PN}_append_gbs +="obmc/gpio/slimsas-cable-1.conf"
SYSTEMD_ENVIRONMENT_FILE_${PN}_append_gbs +="obmc/gpio/slimsas-cable-2.conf"
SYSTEMD_ENVIRONMENT_FILE_${PN}_append_gbs +="obmc/gpio/slimsas-cable-3.conf"
GBS_OBMC_SLIMSAS_CABLE_INSTANCES = "0 1 2 3"

SYSTEMD_LINK_${PN}-presence_append_gbs = " ${@compose_list(d, 'FMT_SLIMSASCABLE', 'GBS_OBMC_SLIMSAS_CABLE_INSTANCES')}"

GBS_SLIMSASCABLE_ENV_FMT  = "obmc/gpio/slimsas-cable-{0}.conf"

SYSTEMD_ENVIRONMENT_FILE_${PN}-presence_gbs = " ${@compose_list(d, 'GBS_SLIMSASCABLE_ENV_FMT', 'GBS_OBMC_SLIMSAS_CABLE_INSTANCES')}"

do_install_append_gbs() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/phosphor-gpio-presence@.service.replace ${D}${systemd_system_unitdir}/phosphor-gpio-presence@.service
}
