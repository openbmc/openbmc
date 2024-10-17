FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

EXTRA_OEMESON:append = " \
                         -Dwarm-reboot=enabled \
                       "

PACKAGECONFIG:append = " no-warm-reboot"
PACKAGECONFIG:remove = "only-run-apr-on-power-loss"

HOST_DEFAULT_TARGETS:remove = " \
    obmc-host-reboot@{}.target.requires/obmc-host-shutdown@{}.target \
    obmc-host-reboot@{}.target.requires/phosphor-reboot-host@{}.service \
    "

CHASSIS_DEFAULT_TARGETS:remove = " \
    obmc-chassis-powerreset@{}.target.requires/phosphor-reset-chassis-on@{}.service \
    obmc-chassis-powerreset@{}.target.requires/phosphor-reset-chassis-running@{}.service \
    obmc-chassis-poweroff@{}.target.requires/obmc-power-stop@{}.service \
    obmc-chassis-poweron@{}.target.requires/obmc-power-start@{}.service \
    "

# When we power off the host, we do not want to do a full chassis power-off
# because that will turn off power to the compute card standby domain (and
# we lose communication with the BIC.
CHASSIS_DEFAULT_TARGETS:remove = " \
    obmc-host-shutdown@{}.target.requires/obmc-chassis-poweroff@{}.target \
    "

SRC_URI:append = " \
    file://chassis-poweroff@.service \
    file://chassis-poweron@.service \
    file://chassis-poweron-failure@.service \
    file://chassis-powercycle@.service \
    file://host-poweroff@.service \
    file://host-poweron@.service \
    file://host-poweron-failure@.service \
    file://host-powercycle@.service \
    file://host-powerreset@.service \
    file://chassis-poweroff \
    file://chassis-poweron \
    file://chassis-poweron-failure \
    file://chassis-powercycle \
    file://host-poweroff \
    file://host-poweron \
    file://host-poweron-failure \
    file://host-powercycle \
    file://host-powerreset \
    file://power-cmd \
    file://wait-until-mctp-connection-done \
    file://rescan-cxl-eid \
    "

RDEPENDS:${PN}:append = " bash"

do_install:append() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/*.service ${D}${systemd_system_unitdir}/

    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${WORKDIR}/chassis-poweroff ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/chassis-poweron ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/chassis-poweron-failure ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/chassis-powercycle ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/host-poweroff ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/host-poweron ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/host-poweron-failure ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/host-powercycle ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/host-powerreset ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/power-cmd ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/wait-until-mctp-connection-done ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/rescan-cxl-eid ${D}${libexecdir}/${PN}/
}

FILES:${PN} += " ${systemd_system_unitdir}/*.service"