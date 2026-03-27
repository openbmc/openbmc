FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:bletchley = " \
    file://obmc/gpio/presence-sled1.conf \
    file://obmc/gpio/presence-sled2.conf \
    file://obmc/gpio/presence-sled3.conf \
    file://obmc/gpio/presence-sled4.conf \
    file://obmc/gpio/presence-sled5.conf \
    file://obmc/gpio/presence-sled6.conf \
    "

SRC_URI:append:bletchley15 = " \
    file://obmc/gpio/presence-sled1.conf \
    file://obmc/gpio/presence-sled2.conf \
    file://obmc/gpio/presence-sled3.conf \
    file://obmc/gpio/presence-sled4.conf \
    "

inherit obmc-phosphor-utils
inherit systemd

# Add service file
SYSTEMD_SERVICE_FMT = "phosphor-gpio-presence@presence-sled{0}.service"
SYSTEMD_SERVICE:${PN}-presence:append = " ${@compose_list(d, 'SYSTEMD_SERVICE_FMT', 'OBMC_HOST_INSTANCES')}"

FILES:${PN}-presence:append = " \
    ${sysconfdir}/default/obmc/gpio/*.conf \
    "

GPIO_PRESENCE_SLED_CONF = "/etc/default/obmc/gpio/%i.conf"

do_install:append(){
    # modify ConditionPathExists and EnvironmentFile to correct filepath
    sed -i -e "s,ConditionPathExists=.*,ConditionPathExists=${GPIO_PRESENCE_SLED_CONF},g" ${D}${systemd_system_unitdir}/phosphor-gpio-presence@.service
    sed -i -e "s,EnvironmentFile=.*,EnvironmentFile=${GPIO_PRESENCE_SLED_CONF},g" ${D}${systemd_system_unitdir}/phosphor-gpio-presence@.service

    for i in ${OBMC_HOST_INSTANCES}; do
        install -d ${D}${sysconfdir}/default/obmc/gpio/
        install -m 0644 ${UNPACKDIR}/obmc/gpio/presence-sled$i.conf \
            ${D}${sysconfdir}/default/obmc/gpio/
    done
}
