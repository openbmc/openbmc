FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd

# Add service file link
TMPL_PRESENT = "phosphor-gpio-presence@.service"
INSTFMT_PRESENT = "phosphor-gpio-presence@presence-sled{0}.service"
PRESENT_TGT = "multi-user.target"
FMT_PRESENT = "../${TMPL_PRESENT}:${PRESENT_TGT}.requires/${INSTFMT_PRESENT}"
SYSTEMD_LINK:${PN}-presence:append = " ${@compose_list(d, 'FMT_PRESENT', 'OBMC_HOST_INSTANCES')}"

# Add environment file
SLED_PRESENT_ENV_FMT = "obmc/gpio/presence-sled{0}.conf"
SYSTEMD_ENVIRONMENT_FILE:${PN}-presence = " ${@compose_list(d, 'SLED_PRESENT_ENV_FMT', 'OBMC_HOST_INSTANCES')}"

GPIO_PRESENCE_SLED_CONF = "/etc/default/obmc/gpio/%i.conf"

do_install:append(){
    # modify ConditionPathExists and EnvironmentFile to correct filepath
    sed -i -e "s,ConditionPathExists=.*,ConditionPathExists=${GPIO_PRESENCE_SLED_CONF},g" ${D}${systemd_system_unitdir}/phosphor-gpio-presence@.service
    sed -i -e "s,EnvironmentFile=.*,EnvironmentFile=${GPIO_PRESENCE_SLED_CONF},g" ${D}${systemd_system_unitdir}/phosphor-gpio-presence@.service
}
