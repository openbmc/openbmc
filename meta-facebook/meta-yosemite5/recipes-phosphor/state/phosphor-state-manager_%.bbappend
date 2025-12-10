FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG:remove = "no-warm-reboot"

# NOTE: The YV5 SCM shares the blade/chassis standby power rail.
# As a result, the chassis is always powered on and does not support
# independent chassis power on/off control; only chassis power cycle
# is supported.

# Chassis ON
CHASSIS_DEFAULT_TARGETS:remove = " \
    obmc-chassis-poweron@{}.target.wants/chassis-poweron@{}.service \
    obmc-chassis-poweron@{}.target.wants/phosphor-reset-host-recovery@{}.service \
    obmc-chassis-poweron@{}.target.requires/obmc-power-start@{}.service \
    obmc-chassis-poweron@{}.target.requires/phosphor-set-chassis-transition-to-on@{}.service \
"

CHASSIS_DEFAULT_TARGETS:append = " \
    obmc-chassis-poweron@{}.target.requires/chassis-poweron@{}.service \
"

# Chassis Off
CHASSIS_DEFAULT_TARGETS:remove = " \
    obmc-chassis-poweroff@{}.target.wants/phosphor-clear-one-time@{}.service \
    obmc-chassis-poweroff@{}.target.requires/obmc-power-stop@{}.service \
    obmc-chassis-poweroff@{}.target.requires/obmc-powered-off@{}.service \
    obmc-chassis-poweroff@{}.target.requires/phosphor-set-chassis-transition-to-off@{}.service \
"

# Chassis Cycle
CHASSIS_DEFAULT_TARGETS:remove = " \
    obmc-chassis-powercycle@{}.target.wants/chassis-powercycle@{}.service \
    obmc-chassis-powerreset@{}.target.requires/phosphor-reset-chassis-on@{}.service \
    obmc-chassis-powerreset@{}.target.requires/phosphor-reset-chassis-running@{}.service \
"

CHASSIS_DEFAULT_TARGETS:append = " \
    obmc-chassis-powercycle@{}.target.requires/chassis-powercycle@{}.service \
"

# Host Reset
HOST_DEFAULT_TARGETS:remove = " \
    obmc-host-warm-reboot@{}.target.requires/xyz.openbmc_project.Ipmi.Internal.SoftPowerOff.service \
    obmc-host-warm-reboot@{}.target.requires/obmc-host-force-warm-reboot@{}.target \
    obmc-host-force-warm-reboot@{}.target.requires/obmc-host-stop@{}.target \
    obmc-host-force-warm-reboot@{}.target.requires/phosphor-reboot-host@{}.service \
"

HOST_DEFAULT_TARGETS:append = " \
    obmc-host-warm-reboot@{}.target.wants/host-powerreset@{}.service \
"

# Host On
HOST_DEFAULT_TARGETS:append = " \
    obmc-host-startmin@{}.target.requires/host-poweron@{}.service \
"

# Host Off
HOST_DEFAULT_TARGETS:append = " \
    obmc-host-shutdown@{}.target.requires/host-graceful-poweroff@{}.service \
    obmc-host-stop@{}.target.requires/host-force-poweroff@{}.service \
"

HOST_DEFAULT_TARGETS:remove = " \
    obmc-host-shutdown@{}.target.wants/host-poweroff@{}.service \
"

# Host Cycle
HOST_DEFAULT_TARGETS:remove = " \
    obmc-host-reboot@{}.target.wants/host-powercycle@{}.service \
    obmc-host-reboot@{}.target.requires/obmc-host-shutdown@{}.service \
"

# We need to ensure that the chassis power is always on.
CHASSIS_DEFAULT_TARGETS:remove = " \
    obmc-host-shutdown@{}.target.requires/obmc-chassis-poweroff@{}.target \
"

# The Yv5 chassis does not support power-off functionality.
HARD_OFF_TMPL_CTRL = ""
HARD_OFF_TGTFMT_CTRL = ""
HARD_OFF_FMT_CTRL = ""
HARD_OFF_INSTFMT_CTRL = ""

STOP_TMPL_CTRL = ""
STOP_TGTFMT_CTRL = ""
STOP_INSTFMT_CTRL = ""
STOP_FMT_CTRL = ""

SYSTEMD_SERVICE:${PN}-chassis:remove = "phosphor-set-chassis-transition-to-on@.service"
SYSTEMD_SERVICE:${PN}-chassis:remove = "phosphor-reset-chassis-on@.service"
SYSTEMD_SERVICE:${PN}-chassis:remove = "phosphor-reset-chassis-running@.service"
SYSTEMD_SERVICE:${PN}-chassis:remove = "obmc-power-start@.service"
SYSTEMD_SERVICE:${PN}-chassis:remove = "obmc-power-stop@.service"

SRC_URI:append = " \
    file://chassis-powercycle \
    file://chassis-powercycle@.service \
    file://chassis-poweron \
    file://chassis-poweron@.service \
    file://discover-sys-init.conf \
    file://host-force-poweroff \
    file://host-force-poweroff@.service \
    file://host-graceful-poweroff \
    file://host-graceful-poweroff@.service \
    file://host-poweron \
    file://host-poweron@.service \
    file://host-powerreset \
    file://host-powerreset@.service \
    file://power-cmd \
    file://phosphor-state-manager-init.conf \
"

SRC_URI:append:aspeed-g6 = " file://ast2600/phosphor-state-manager-init"
SRC_URI:append:aspeed-g7 = " file://ast2700/phosphor-state-manager-init"

RDEPENDS:${PN}:append = " bash"

do_install:append() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/*.service ${D}${systemd_system_unitdir}/

    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/chassis-powercycle ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/chassis-poweron ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/host-force-poweroff ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/host-graceful-poweroff ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/host-poweron ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/host-powerreset ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/power-cmd ${D}${libexecdir}/${PN}/
}

do_install:append:aspeed-g6() {
    install -m 0755 ${UNPACKDIR}/ast2600/phosphor-state-manager-init ${D}${libexecdir}/${PN}/
}
do_install:append:aspeed-g7() {
    install -m 0755 ${UNPACKDIR}/ast2700/phosphor-state-manager-init ${D}${libexecdir}/${PN}/
}
SYSTEMD_OVERRIDE:${PN}-discover += "discover-sys-init.conf:phosphor-discover-system-state@0.service.d/discover-sys-init.conf"
SYSTEMD_OVERRIDE:${PN}-systemd-target-monitor += "phosphor-state-manager-init.conf:phosphor-systemd-target-monitor.service.d/phosphor-state-manager-init.conf"
