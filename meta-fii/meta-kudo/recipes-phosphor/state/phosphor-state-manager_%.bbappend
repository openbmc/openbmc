FILESEXTRAPATHS:prepend:kudo := "${THISDIR}/${PN}:"

DEPENDS:append:kudo = " gpioplus"

STATE_MGR_PACKAGES:remove:kudo = " ${PN}-host-check"
RRECOMMENDS:${PN}-host:remove:kudo = " ${PN}-host-check"
PACKAGECONFIG:append:kudo = " host-gpio"

FILES:${PN}:append:kudo = " ${systemd_system_unitdir}/*"

pkg_postinst:${PN}-obmc-targets:append:kudo() {
    rm "$D$systemd_system_unitdir/obmc-host-shutdown@0.target.requires/obmc-chassis-poweroff@0.target"

    rm "$D$systemd_system_unitdir/obmc-host-warm-reboot@0.target.requires/xyz.openbmc_project.Ipmi.Internal.SoftPowerOff.service"
    rm "$D$systemd_system_unitdir/obmc-host-warm-reboot@0.target.requires/obmc-host-force-warm-reboot@0.target"

    rm "$D$systemd_system_unitdir/obmc-host-reboot@0.target.requires/phosphor-reboot-host@0.service"
    rm "$D$systemd_system_unitdir/obmc-host-reboot@0.target.requires/obmc-host-shutdown@0.target"

    rm "$D$systemd_system_unitdir/obmc-host-force-warm-reboot@0.target.requires/obmc-host-stop@0.target"
    rm "$D$systemd_system_unitdir/obmc-host-force-warm-reboot@0.target.requires/phosphor-reboot-host@0.service"

    rm "$D$systemd_system_unitdir/obmc-host-reset@0.target.requires/phosphor-reset-host-running@0.service"
}

SRC_URI:append:kudo = " \
    file://xyz.openbmc_project.State.Chassis@.service \
    file://xyz.openbmc_project.State.Host@.service \
    "

do_install:append:kudo() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/xyz.openbmc_project.State.Chassis@.service ${D}${systemd_system_unitdir}/xyz.openbmc_project.State.Chassis@.service
    install -m 0644 ${UNPACKDIR}/xyz.openbmc_project.State.Host@.service ${D}${systemd_system_unitdir}/xyz.openbmc_project.State.Host@.service
    rm -f ${D}${systemd_system_unitdir}/phosphor-reset-host-check@.service
    rm -f ${D}${systemd_system_unitdir}/phosphor-reset-host-running@.service
}

