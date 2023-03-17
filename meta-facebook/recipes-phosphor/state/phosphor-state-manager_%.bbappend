FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}/${MACHINE}:"

HOST_DEFAULT_TARGETS:append = " \
    obmc-host-shutdown@{}.target.wants/host-poweroff@{}.service \
    obmc-host-start@{}.target.wants/host-poweron@{}.service \
    obmc-host-reboot@{}.target.wants/host-powercycle@{}.service \
    obmc-host-force-warm-reboot@{}.target.wants/host-powerreset@{}.service \
"

CHASSIS_DEFAULT_TARGETS:append = " \
    obmc-chassis-poweron@{}.target.wants/chassis-poweron@{}.service \
    obmc-chassis-hard-poweroff@{}.target.wants/chassis-poweroff@{}.service \
    obmc-chassis-powercycle@{}.target.wants/chassis-powercycle@{}.service \
"

CHASSIS_DEFAULT_TARGETS:remove = " \
    obmc-chassis-poweroff@{}.target.requires/obmc-powered-off@{}.service \
"

CHASSIS_DEFAULT_TARGETS:remove:greatlakes = " \
    obmc-chassis-poweroff@{}.target.requires/obmc-power-stop@{}.service \
    obmc-chassis-poweron@{}.target.requires/obmc-power-start@{}.service \
    "

SRC_URI:append:greatlakes = " \
    file://chassis-poweroff@.service \
    file://chassis-poweron@.service \
    file://chassis-powercycle@.service \
    file://host-poweroff@.service \
    file://host-poweron@.service \
    file://host-powercycle@.service \
    file://host-powerreset@.service \
    file://power-ctrl-init.service \
    file://chassis-poweroff \
    file://chassis-poweron \
    file://chassis-powercycle \
    file://host-poweroff \
    file://host-poweron \
    file://host-powercycle \
    file://host-powerreset \
    file://power-cmd \
    file://power-ctrl-init \
    "

RDEPENDS:${PN}:append:greatlakes = " bash"

do_install:append:greatlakes() {
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
    install -m 0777 ${WORKDIR}/power-ctrl-init ${D}${libexecdir}/${PN}/
}

FILES:${PN} += " ${systemd_system_unitdir}/*.service"

SYSTEMD_SERVICE:${PN}-bmc:append:greatlakes = "power-ctrl-init.service"
