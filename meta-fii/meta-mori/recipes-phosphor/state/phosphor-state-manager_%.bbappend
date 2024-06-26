FILESEXTRAPATHS:prepend:mori := "${THISDIR}/${PN}:"
PACKAGECONFIG:append:mori = " host-gpio"

SRC_URI:append:mori = " \
    file://xyz.openbmc_project.State.Chassis@.service \
    file://xyz.openbmc_project.State.Host@.service \
"

do_install:append:mori() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/xyz.openbmc_project.State.Chassis@.service \
        ${D}${systemd_system_unitdir}/xyz.openbmc_project.State.Chassis@.service
    install -m 0644 ${UNPACKDIR}/xyz.openbmc_project.State.Host@.service \
        ${D}${systemd_system_unitdir}/xyz.openbmc_project.State.Host@.service
}

FILES:${PN}:append:mori = " ${systemd_system_unitdir}/*"

pkg_postinst:${PN}-obmc-targets:append:mori() {
    rm "$D$systemd_system_unitdir/obmc-host-reboot@0.target.requires/obmc-host-shutdown@0.target"
    rm "$D$systemd_system_unitdir/obmc-host-reboot@0.target.requires/phosphor-reboot-host@0.service"
    rm "$D$systemd_system_unitdir/obmc-host-shutdown@0.target.requires/obmc-chassis-poweroff@0.target"
    rm "$D$systemd_system_unitdir/obmc-host-warm-reboot@0.target.requires/obmc-host-force-warm-reboot@0.target"
    rm "$D$systemd_system_unitdir/obmc-host-warm-reboot@0.target.requires/xyz.openbmc_project.Ipmi.Internal.SoftPowerOff.service"
    rm "$D$systemd_system_unitdir/obmc-host-force-warm-reboot@0.target.requires/obmc-host-stop@0.target"
    rm "$D$systemd_system_unitdir/obmc-host-force-warm-reboot@0.target.requires/phosphor-reboot-host@0.service"
    rm "$D$systemd_system_unitdir/multi-user.target.requires/obmc-host-reset@0.target"
}
