FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

CHASSIS_DEFAULT_TARGETS:remove = " \
    obmc-chassis-poweroff@{}.target.requires/obmc-power-stop@{}.service \
    obmc-chassis-poweron@{}.target.requires/obmc-power-start@{}.service \
    "

SRC_URI:append = " \
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

RDEPENDS:${PN}:append = " bash"

do_install:append () {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/*.service ${D}${systemd_system_unitdir}/

    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${WORKDIR}/chassis-poweroff ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/chassis-poweron ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/chassis-powercycle ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/host-poweroff ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/host-poweron ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/host-powercycle ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/host-powerreset ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/power-cmd ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/power-ctrl-init ${D}${libexecdir}/${PN}/
}

SYSTEMD_SERVICE:${PN}-bmc:append = "power-ctrl-init.service"
