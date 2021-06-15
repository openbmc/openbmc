pkg_postinst_${PN}-obmc-targets_append() {

    mkdir -p $D$systemd_system_unitdir/multi-user.target.requires
    LINK="$D$systemd_system_unitdir/multi-user.target.requires/obmc-host-reset@0.target"
    TARGET="../obmc-host-reset@.target"
    ln -s $TARGET $LINK

    LINK="$D$systemd_system_unitdir/multi-user.target.requires/phosphor-discover-system-state@0.service"
    TARGET="../phosphor-discover-system-state@.service"
    ln -s $TARGET $LINK

    mkdir -p $D$systemd_system_unitdir/obmc-host-start@0.target.requires
    LINK="$D$systemd_system_unitdir/obmc-host-start@0.target.requires/obmc-host-startmin@0.target"
    TARGET="../obmc-host-startmin@.target"
    ln -s $TARGET $LINK

    LINK="$D$systemd_system_unitdir/obmc-host-start@0.target.requires/phosphor-reset-host-reboot-attempts@0.service"
    TARGET="../phosphor-reset-host-reboot-attempts@.service"
    ln -s $TARGET $LINK

    mkdir -p $D$systemd_system_unitdir/obmc-host-startmin@0.target.wants
    LINK="$D$systemd_system_unitdir/obmc-host-startmin@0.target.wants/phosphor-set-host-transition-to-running@0.service"
    TARGET="../phosphor-set-host-transition-to-running@.service"
    ln -s $TARGET $LINK

    mkdir -p $D$systemd_system_unitdir/obmc-host-stop@0.target.wants
    LINK="$D$systemd_system_unitdir/obmc-host-stop@0.target.wants/phosphor-set-host-transition-to-off@0.service"
    TARGET="../phosphor-set-host-transition-to-off@.service"
    ln -s $TARGET $LINK

    mkdir -p $D$systemd_system_unitdir/obmc-host-reset@0.target.requires
    LINK="$D$systemd_system_unitdir/obmc-host-reset@0.target.requires/phosphor-reset-host-check@0.service"
    TARGET="../phosphor-reset-host-check@.service"
    ln -s $TARGET $LINK

    LINK="$D$systemd_system_unitdir/obmc-host-reset@0.target.requires/phosphor-reset-sensor-states@0.service"
    TARGET="../phosphor-reset-sensor-states@.service"
    ln -s $TARGET $LINK

    LINK="$D$systemd_system_unitdir/obmc-host-reset@0.target.requires/phosphor-reset-host-running@0.service"
    TARGET="../phosphor-reset-host-running@.service"
    ln -s $TARGET $LINK

    mkdir -p $D$systemd_system_unitdir/obmc-host-stop@0.target.wants
    LINK="$D$systemd_system_unitdir/obmc-host-stop@0.target.wants/phosphor-reset-sensor-states@0.service"
    TARGET="../phosphor-reset-sensor-states@.service"
    ln -s $TARGET $LINK

    mkdir -p $D$systemd_system_unitdir/obmc-host-quiesce@0.target.wants
    LINK="$D$systemd_system_unitdir/obmc-host-quiesce@0.target.wants/phosphor-reset-sensor-states@0.service"
    TARGET="../phosphor-reset-sensor-states@.service"
    ln -s $TARGET $LINK

    mkdir -p $D$systemd_system_unitdir/obmc-host-shutdown@0.target.requires
    LINK="$D$systemd_system_unitdir/obmc-host-shutdown@0.target.requires/obmc-chassis-poweroff@0.target"
    TARGET="../obmc-chassis-poweroff@.target"
    ln -s $TARGET $LINK

    mkdir -p $D$systemd_system_unitdir/obmc-host-reboot@0.target.requires
    LINK="$D$systemd_system_unitdir/obmc-host-reboot@0.target.requires/obmc-host-shutdown@0.target"
    TARGET="../obmc-host-shutdown@.target"
    ln -s $TARGET $LINK

    LINK="$D$systemd_system_unitdir/obmc-host-reboot@0.target.requires/phosphor-reboot-host@0.service"
    TARGET="../phosphor-reboot-host@.service"
    ln -s $TARGET $LINK

    mkdir -p $D$systemd_system_unitdir/obmc-host-warm-reboot@0.target.requires
    LINK="$D$systemd_system_unitdir/obmc-host-warm-reboot@0.target.requires/xyz.openbmc_project.Ipmi.Internal.SoftPowerOff.service"
    TARGET="../xyz.openbmc_project.Ipmi.Internal.SoftPowerOff.service"
    ln -s $TARGET $LINK

    mkdir -p $D$systemd_system_unitdir/obmc-host-force-warm-reboot@0.target.requires
    LINK="$D$systemd_system_unitdir/obmc-host-force-warm-reboot@0.target.requires/obmc-host-stop@0.target"
    TARGET="../obmc-host-stop@.target"
    ln -s $TARGET $LINK

    LINK="$D$systemd_system_unitdir/obmc-host-force-warm-reboot@0.target.requires/phosphor-reboot-host@0.service"
    TARGET="../phosphor-reboot-host@.service"
    ln -s $TARGET $LINK

    LINK="$D$systemd_system_unitdir/obmc-host-warm-reboot@0.target.requires/obmc-host-force-warm-reboot@0.target"
    TARGET="../obmc-host-force-warm-reboot@.target"
    ln -s $TARGET $LINK

    mkdir -p $D$systemd_system_unitdir/obmc-chassis-poweroff@0.target.wants
    LINK="$D$systemd_system_unitdir/obmc-chassis-poweroff@0.target.wants/phosphor-clear-one-time@0.service"
    TARGET="../phosphor-clear-one-time@.service"
    ln -s $TARGET $LINK
}

pkg_prerm_${PN}-obmc-targets_append() {

    LINK="$D$systemd_system_unitdir/multi-user.target.requires/obmc-host-reset@0.target"
    rm $LINK

    LINK="$D$systemd_system_unitdir/multi-user.target.requires/phosphor-discover-system-state@0.service"
    rm $LINK

    LINK="$D$systemd_system_unitdir/obmc-host-start@0.target.requires/obmc-host-startmin@0.target"
    rm $LINK

    LINK="$D$systemd_system_unitdir/obmc-host-start@0.target.requires/phosphor-reset-host-reboot-attempts@0.service"
    rm $LINK

    LINK="$D$systemd_system_unitdir/obmc-host-startmin@0.target.wants/phosphor-set-host-transition-to-running@0.service"
    rm $LINK

    LINK="$D$systemd_system_unitdir/obmc-host-stop@0.target.wants/phosphor-set-host-transition-to-off@0.service"
    rm $LINK

    LINK="$D$systemd_system_unitdir/obmc-host-reset@0.target.requires/phosphor-reset-host-check@0.service"
    rm $LINK

    LINK="$D$systemd_system_unitdir/obmc-host-reset@0.target.requires/phosphor-reset-sensor-states@0.service"
    rm $LINK

    LINK="$D$systemd_system_unitdir/obmc-host-stop@0.target.wants/phosphor-reset-sensor-states@0.service"
    rm $LINK

    LINK="$D$systemd_system_unitdir/obmc-host-quiesce@0.target.wants/phosphor-reset-sensor-states@0.service"
    rm $LINK

    LINK="$D$systemd_system_unitdir/obmc-host-reset@0.target.requires/phosphor-reset-host-running@0.service"
    rm $LINK

    LINK="$D$systemd_system_unitdir/obmc-host-shutdown@0.target.requires/obmc-chassis-poweroff@0.target"
    rm $LINK

    LINK="$D$systemd_system_unitdir/obmc-host-reboot@0.target.requires/obmc-host-shutdown@0.target"
    rm $LINK

    LINK="$D$systemd_system_unitdir/obmc-host-reboot@0.target.requires/phosphor-reboot-host@0.service"
    rm $LINK

    LINK="$D$systemd_system_unitdir/obmc-host-warm-reboot@0.target.requires/xyz.openbmc_project.Ipmi.Internal.SoftPowerOff.service"
    rm $LINK

    LINK="$D$systemd_system_unitdir/obmc-host-force-warm-reboot@0.target.requires/obmc-host-stop@0.target"
    rm $LINK

    LINK="$D$systemd_system_unitdir/obmc-host-force-warm-reboot@0.target.requires/phosphor-reboot-host@0.service"
    rm $LINK

    LINK="$D$systemd_system_unitdir/obmc-host-warm-reboot@0.target.requires/obmc-host-force-warm-reboot@0.target"
    rm $LINK

    LINK="$D$systemd_system_unitdir/obmc-chassis-poweroff@0.target.wants/phosphor-clear-one-time@0.service"
    rm $LINK
}
