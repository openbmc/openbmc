FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG:remove = "no-warm-reboot"

CHASSIS_DEFAULT_TARGETS:remove:harma = " \
    obmc-chassis-powerreset@{}.target.requires/phosphor-reset-chassis-on@{}.service \
    obmc-chassis-powerreset@{}.target.requires/phosphor-reset-chassis-running@{}.service \
    obmc-chassis-poweroff@{}.target.requires/obmc-power-stop@{}.service \
    obmc-chassis-poweron@{}.target.requires/obmc-power-start@{}.service \
    "

CHASSIS_DEFAULT_TARGETS:append:harma = " \
    obmc-chassis-hard-poweroff@{}.target.wants/host-poweroff@0.service \
    "

HOST_DEFAULT_TARGETS:remove:harma = " \
    obmc-host-start@{}.target.wants/host-poweron@{}.service \
"

HOST_DEFAULT_TARGETS:append:harma = " \
    obmc-host-startmin@{}.target.requires/host-poweron@{}.service \
"

SRC_URI:append:harma = " \
    file://chassis-powercycle@.service \
    file://host-poweroff@.service \
    file://host-poweron@.service \
    file://host-powercycle@.service \
    file://host-powerreset@.service \
    file://chassis-powercycle \
    file://host-poweroff \
    file://host-poweron \
    file://host-powercycle \
    file://host-powerreset \
    file://power-cmd \
    file://discover-sys-init.conf \
    file://phosphor-state-manager-init \
    file://phosphor-state-manager-init.conf \
    "

RDEPENDS:${PN}:append:harma = " bash"

do_install:append:harma() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/*.service ${D}${systemd_system_unitdir}/

    install -d ${D}${libexecdir}/${PN}
    install -m 0777 ${WORKDIR}/chassis-powercycle ${D}${libexecdir}/${PN}/
    install -m 0777 ${WORKDIR}/host-poweroff ${D}${libexecdir}/${PN}/
    install -m 0777 ${WORKDIR}/host-poweron ${D}${libexecdir}/${PN}/
    install -m 0777 ${WORKDIR}/host-powercycle ${D}${libexecdir}/${PN}/
    install -m 0777 ${WORKDIR}/host-powerreset ${D}${libexecdir}/${PN}/
    install -m 0777 ${WORKDIR}/power-cmd ${D}${libexecdir}/${PN}/
    install -m 0777 ${WORKDIR}/phosphor-state-manager-init ${D}${libexecdir}/${PN}/
}
SYSTEMD_OVERRIDE:${PN}-discover:harma += "discover-sys-init.conf:phosphor-discover-system-state@0.service.d/discover-sys-init.conf"
SYSTEMD_OVERRIDE:${PN}-systemd-target-monitor:harma += "phosphor-state-manager-init.conf:phosphor-systemd-target-monitor.service.d/phosphor-state-manager-init.conf"
