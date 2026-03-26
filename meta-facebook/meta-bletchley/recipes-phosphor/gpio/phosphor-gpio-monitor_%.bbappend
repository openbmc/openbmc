FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-utils
inherit systemd

# Add service file
SYSTEMD_SERVICE_FMT = "phosphor-gpio-presence@presence-sled{0}.service"
SYSTEMD_SERVICE:${PN}-presence:append = " ${@compose_list(d, 'SYSTEMD_SERVICE_FMT', 'OBMC_HOST_INSTANCES')}"

# Add environment file
SLED_PRESENT_ENV_FMT = "obmc/gpio/presence-sled{0}.conf"
SYSTEMD_ENVIRONMENT_FILE:${PN}-presence = " ${@compose_list(d, 'SLED_PRESENT_ENV_FMT', 'OBMC_HOST_INSTANCES')}"

GPIO_PRESENCE_SLED_CONF = "/etc/default/obmc/gpio/%i.conf"

do_install:append(){
    # modify ConditionPathExists and EnvironmentFile to correct filepath
    sed -i -e "s,ConditionPathExists=.*,ConditionPathExists=${GPIO_PRESENCE_SLED_CONF},g" ${D}${systemd_system_unitdir}/phosphor-gpio-presence@.service
    sed -i -e "s,EnvironmentFile=.*,EnvironmentFile=${GPIO_PRESENCE_SLED_CONF},g" ${D}${systemd_system_unitdir}/phosphor-gpio-presence@.service
}
