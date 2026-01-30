FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
PACKAGECONFIG:append = " host-gpio"

CHASSIS_DEFAULT_TARGETS:remove = " \
    obmc-chassis-powerreset@{}.target.requires/phosphor-reset-chassis-on@{}.service \
    obmc-chassis-powerreset@{}.target.requires/phosphor-reset-chassis-running@{}.service \
    obmc-chassis-poweroff@{}.target.requires/obmc-power-stop@{}.service \
    obmc-chassis-poweron@{}.target.requires/obmc-power-start@{}.service \
"

# Host Config
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

RDEPENDS:${PN}:append = " bash"

RRECOMMENDS:${PN}-chassis:remove = " ${PN}-chassis-poweron-log"

SYSTEMD_SERVICE:${PN}-chassis:remove = "phosphor-reset-chassis-on@.service"
SYSTEMD_SERVICE:${PN}-chassis:remove = "phosphor-reset-chassis-running@.service"
SYSTEMD_SERVICE:${PN}-chassis:remove = "obmc-power-start@.service"
SYSTEMD_SERVICE:${PN}-chassis:remove = "obmc-power-stop@.service"

SRC_URI:append = " \
    file://chassis-powercycle \
    file://chassis-powercycle@.service \
    file://host-force-poweroff \
    file://host-force-poweroff@.service \
    file://host-graceful-poweroff \
    file://host-graceful-poweroff@.service \
    file://host-poweron \
    file://host-poweron@.service \
    file://host-powerreset \
    file://host-powerreset@.service \
    file://power-cmd \
    "

do_install:append() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/*.service ${D}${systemd_system_unitdir}/

    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/chassis-powercycle ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/host-force-poweroff ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/host-graceful-poweroff ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/host-poweron ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/host-powerreset ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/power-cmd ${D}${libexecdir}/${PN}/
}
