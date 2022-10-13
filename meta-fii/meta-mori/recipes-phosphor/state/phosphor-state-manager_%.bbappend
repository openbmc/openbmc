FILESEXTRAPATHS:prepend:mori := "${THISDIR}/${PN}:"

DEPENDS:append:mori = " gpioplus"
EXTRA_OEMESON:append = " -Dhost-gpios=enabled"

FILES:${PN}:append:mori = " ${systemd_system_unitdir}/*"
FILES:${PN}-host:append:mori = " ${bindir}/phosphor-host-condition-gpio"
SYSTEMD_SERVICE:${PN}-host:append:mori = " phosphor-host-condition-gpio@.service"

pkg_postinst:${PN}-obmc-targets:prepend:mori() {
    mkdir -p $D$systemd_system_unitdir/multi-user.target.requires
    LINK="$D$systemd_system_unitdir/multi-user.target.requires/phosphor-host-condition-gpio@0.service"
    TARGET="../phosphor-host-condition-gpio@.service"
    ln -s $TARGET $LINK
}

pkg_postinst:${PN}-obmc-targets:append:mori() {
    rm "$D$systemd_system_unitdir/obmc-host-reboot@0.target.requires/obmc-host-shutdown@0.target"
    rm "$D$systemd_system_unitdir/obmc-host-reboot@0.target.requires/phosphor-reboot-host@0.service"
}


pkg_prerm:${PN}-obmc-targets:prepend:mori() {
    LINK="$D$systemd_system_unitdir/multi-user.target.requires/phosphor-host-condition-gpio@0.service"
    rm $LINK
}

SRC_URI:append:mori = " \
    file://xyz.openbmc_project.State.Chassis@.service \
    file://xyz.openbmc_project.State.Host@.service \
    "

do_install:append:mori() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/xyz.openbmc_project.State.Chassis@.service ${D}${systemd_system_unitdir}/xyz.openbmc_project.State.Chassis@.service
    install -m 0644 ${WORKDIR}/xyz.openbmc_project.State.Host@.service ${D}${systemd_system_unitdir}/xyz.openbmc_project.State.Host@.service
}
