#
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG:remove = "only-run-apr-on-power-loss"
PACKAGECONFIG:append = " host-gpio"

# Chassis Config
# TODO: Remove it when 69903 applied
CHASSIS_DEFAULT_TARGETS:remove = " \
    obmc-chassis-powerreset@{}.target.requires/phosphor-reset-chassis-on@{}.service \
    obmc-chassis-powerreset@{}.target.requires/phosphor-reset-chassis-running@{}.service \
    obmc-chassis-poweroff@{}.target.requires/obmc-power-stop@{}.service \
    obmc-chassis-poweron@{}.target.requires/obmc-power-start@{}.service \
    "

# TODO: Remove it when 69903 applied
CHASSIS_DEFAULT_TARGETS:remove = " \
    obmc-chassis-poweron@{}.target.wants/chassis-poweron@{}.service \
    obmc-chassis-hard-poweroff@{}.target.wants/chassis-poweroff@{}.service \
    obmc-chassis-powercycle@{}.target.wants/chassis-powercycle@{}.service \
    "

# TODO: Remove it when 69903 applied
CHASSIS_DEFAULT_TARGETS:append = " \
    obmc-chassis-poweron@{}.target.requires/chassis-poweron@{}.service \
    obmc-chassis-powercycle@{}.target.requires/chassis-powercycle@{}.service \
    "
# TODO: Remove it when 69903 commit
CHASSIS_DEFAULT_TARGETS:append = " \
    obmc-chassis-poweroff@{}.target.requires/obmc-powered-off@{}.service \
    "

# Host Config
# Host Reset
HOST_DEFAULT_TARGETS:remove = " \
    obmc-host-warm-reboot@{}.target.requires/xyz.openbmc_project.Ipmi.Internal.SoftPowerOff.service \
    obmc-host-warm-reboot@{}.target.wants/pldmSoftPowerOff.service \
    obmc-host-force-warm-reboot@{}.target.requires/obmc-host-stop@{}.target \
    obmc-host-force-warm-reboot@{}.target.requires/phosphor-reboot-host@{}.service \
    "

# Host On/Off
HOST_DEFAULT_TARGETS:append = " \
    obmc-host-startmin@{}.target.requires/host-poweron@{}.service \
    "

HOST_DEFAULT_TARGETS:append = " \
    obmc-host-shutdown@{}.target.requires/host-graceful-poweroff@{}.service \
    obmc-host-stop@{}.target.requires/host-force-poweroff@{}.service \
    "

HOST_DEFAULT_TARGETS:remove = " \
    obmc-host-shutdown@{}.target.wants/host-poweroff@{}.service \
    obmc-host-start@{}.target.wants/host-poweron@{}.service \
    "

# Host Cycle
HOST_DEFAULT_TARGETS:remove = " \
    obmc-host-reboot@{}.target.wants/host-powercycle@{}.service \
    obmc-host-reboot@{}.target.requires/obmc-host-shutdown@{}.service \
    "

#We need to ensure that the chassis power is always on.
CHASSIS_DEFAULT_TARGETS:remove = " \
    obmc-host-shutdown@{}.target.requires/obmc-chassis-poweroff@{}.target \
    "

HARD_OFF_TMPL_CTRL=""
HARD_OFF_TGTFMT_CTRL=""
HARD_OFF_FMT_CTRL=""
HARD_OFF_INSTFMT_CTRL=""


SRC_URI:append = " \
    file://chassis-powercycle \
    file://chassis-powercycle@.service \
    file://chassis-poweroff \
    file://chassis-poweroff@.service \
    file://chassis-poweron \
    file://chassis-poweron@.service \
    file://host-force-poweroff \
    file://host-force-poweroff@.service \
    file://host-graceful-poweroff \
    file://host-graceful-poweroff@.service \
    file://host-poweron \
    file://host-poweron@.service \
    file://host-powerreset \
    file://host-powerreset@.service \
    file://power-cmd \
    file://phosphor-wait-power-off@.service \
    file://discover-sys-init.conf \
    file://phosphor-state-manager-init \
    file://phosphor-state-manager-init.conf \
    "

RDEPENDS:${PN}:append = " bash"

do_install:append() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/*.service ${D}${systemd_system_unitdir}/

    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${WORKDIR}/chassis-poweroff ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/chassis-poweron ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/chassis-powercycle ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/host-force-poweroff ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/host-graceful-poweroff ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/host-poweron ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/host-powerreset ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/power-cmd ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/phosphor-state-manager-init ${D}${libexecdir}/${PN}/
}
SYSTEMD_OVERRIDE:${PN}-discover += "discover-sys-init.conf:phosphor-discover-system-state@0.service.d/discover-sys-init.conf"
SYSTEMD_OVERRIDE:${PN}-systemd-target-monitor += "phosphor-state-manager-init.conf:phosphor-systemd-target-monitor.service.d/phosphor-state-manager-init.conf"
