FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

EXTRA_OEMESON:append = " \
                         -Dwarm-reboot=enabled \
                       "

CHASSIS_DEFAULT_TARGETS:remove:yosemite4 = " \
    obmc-chassis-powerreset@{}.target.requires/phosphor-reset-chassis-on@{}.service \
    obmc-chassis-powerreset@{}.target.requires/phosphor-reset-chassis-running@{}.service \
    obmc-chassis-poweroff@{}.target.requires/obmc-power-stop@{}.service \
    obmc-chassis-poweron@{}.target.requires/obmc-power-start@{}.service \
    "

SRC_URI:append:yosemite4 = " \
    file://chassis-poweroff@.service \
    file://chassis-poweron@.service \
    file://chassis-powercycle@.service \
    file://host-poweroff@.service \
    file://host-poweron@.service \
    file://host-powercycle@.service \
    file://host-powerreset@.service \
    file://chassis-poweroff \
    file://chassis-poweron \
    file://chassis-powercycle \
    file://host-poweroff \
    file://host-poweron \
    file://host-powercycle \
    file://host-powerreset \
    file://power-cmd \
    "

RDEPENDS:${PN}:append:yosemite4 = " bash"

do_install:append:yosemite4() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/*.service ${D}${systemd_system_unitdir}/

    install -d ${D}${libexecdir}/${PN}
    install -m 0777 ${WORKDIR}/chassis-poweroff ${D}${libexecdir}/${PN}/
    install -m 0777 ${WORKDIR}/chassis-poweron ${D}${libexecdir}/${PN}/
    install -m 0777 ${WORKDIR}/chassis-powercycle ${D}${libexecdir}/${PN}/
    install -m 0777 ${WORKDIR}/host-poweroff ${D}${libexecdir}/${PN}/
    install -m 0777 ${WORKDIR}/host-poweron ${D}${libexecdir}/${PN}/
    install -m 0777 ${WORKDIR}/host-powercycle ${D}${libexecdir}/${PN}/
    install -m 0777 ${WORKDIR}/host-powerreset ${D}${libexecdir}/${PN}/
    install -m 0777 ${WORKDIR}/power-cmd ${D}${libexecdir}/${PN}/
}

FILES:${PN} += " ${systemd_system_unitdir}/*.service"
