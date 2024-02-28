FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG:remove = "only-run-apr-on-power-loss"

CHASSIS_DEFAULT_TARGETS:remove = " \
    obmc-chassis-powerreset@{}.target.requires/phosphor-reset-chassis-on@{}.service \
    obmc-chassis-powerreset@{}.target.requires/phosphor-reset-chassis-running@{}.service \
    obmc-chassis-poweroff@{}.target.requires/obmc-power-stop@{}.service \
    obmc-chassis-poweron@{}.target.requires/obmc-power-start@{}.service \
    "

CHASSIS_DEFAULT_TARGETS:append = " \
    obmc-chassis-hard-poweroff@{}.target.wants/host-poweroff@0.service \
    "

HOST_DEFAULT_TARGETS:remove = " \
    obmc-host-start@{}.target.wants/host-poweron@{}.service \
    obmc-host-force-warm-reboot@{}.target.requires/obmc-host-stop@{}.target \
    obmc-host-force-warm-reboot@{}.target.requires/phosphor-reboot-host@{}.service \
"

HOST_DEFAULT_TARGETS:append = " \
    obmc-host-startmin@{}.target.requires/host-poweron@{}.service \
"

SRC_URI:append = " \
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

RDEPENDS:${PN}:append = " bash"

do_install:append() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/*.service ${D}${systemd_system_unitdir}/

    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${WORKDIR}/chassis-powercycle ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/host-poweroff ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/host-poweron ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/host-powercycle ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/host-powerreset ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/power-cmd ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/phosphor-state-manager-init ${D}${libexecdir}/${PN}/
}
SYSTEMD_OVERRIDE:${PN}-discover += "discover-sys-init.conf:phosphor-discover-system-state@0.service.d/discover-sys-init.conf"
SYSTEMD_OVERRIDE:${PN}-systemd-target-monitor += "phosphor-state-manager-init.conf:phosphor-systemd-target-monitor.service.d/phosphor-state-manager-init.conf"
