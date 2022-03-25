
# TODO: There are some targets that are chassis oriented, but there is no
#       obvious way to map HOST<->CHASSIS relationships.  Making an assumption
#       currently that there is a 1-to-1 relationship between the two.

pkg_postinst:${PN}-obmc-targets:append() {

    for i in ${OBMC_HOST_INSTANCES};
    do
        mkdir -p $D$systemd_system_unitdir/multi-user.target.requires
        LINK="$D$systemd_system_unitdir/multi-user.target.requires/xyz.openbmc_project.State.Host@${i}.service"
        TARGET="../xyz.openbmc_project.State.Host@.service"
        ln -s $TARGET $LINK

        LINK="$D$systemd_system_unitdir/multi-user.target.requires/xyz.openbmc_project.State.Chassis@${i}.service"
        TARGET="../xyz.openbmc_project.State.Chassis@.service"
        ln -s $TARGET $LINK

        LINK="$D$systemd_system_unitdir/multi-user.target.requires/obmc-host-reset@${i}.target"
        TARGET="../obmc-host-reset@.target"
        ln -s $TARGET $LINK

        LINK="$D$systemd_system_unitdir/multi-user.target.requires/phosphor-discover-system-state@${i}.service"
        TARGET="../phosphor-discover-system-state@.service"
        ln -s $TARGET $LINK

        LINK="$D$systemd_system_unitdir/obmc-bmc-service-quiesce@${i}.target"
        TARGET="./obmc-bmc-service-quiesce@.target"
        ln -s $TARGET $LINK

        mkdir -p $D$systemd_system_unitdir/obmc-chassis-poweron@${i}.target.wants
        LINK="$D$systemd_system_unitdir/obmc-chassis-poweron@${i}.target.wants/phosphor-reset-host-recovery@${i}.service"
        TARGET="../phosphor-reset-host-recovery@.service"
        ln -s $TARGET $LINK

        mkdir -p $D$systemd_system_unitdir/obmc-host-start@${i}.target.requires
        LINK="$D$systemd_system_unitdir/obmc-host-start@${i}.target.requires/obmc-host-startmin@${i}.target"
        TARGET="../obmc-host-startmin@.target"
        ln -s $TARGET $LINK

        LINK="$D$systemd_system_unitdir/obmc-host-start@${i}.target.requires/phosphor-reset-host-reboot-attempts@${i}.service"
        TARGET="../phosphor-reset-host-reboot-attempts@.service"
        ln -s $TARGET $LINK

        mkdir -p $D$systemd_system_unitdir/obmc-host-startmin@${i}.target.wants
        LINK="$D$systemd_system_unitdir/obmc-host-startmin@${i}.target.wants/phosphor-set-host-transition-to-running@${i}.service"
        TARGET="../phosphor-set-host-transition-to-running@.service"
        ln -s $TARGET $LINK

        mkdir -p $D$systemd_system_unitdir/obmc-host-stop@${i}.target.wants
        LINK="$D$systemd_system_unitdir/obmc-host-stop@${i}.target.wants/phosphor-set-host-transition-to-off@${i}.service"
        TARGET="../phosphor-set-host-transition-to-off@.service"
        ln -s $TARGET $LINK

        mkdir -p $D$systemd_system_unitdir/obmc-host-reset@${i}.target.requires
        LINK="$D$systemd_system_unitdir/obmc-host-reset@${i}.target.requires/phosphor-reset-host-running@${i}.service"
        TARGET="../phosphor-reset-host-running@.service"
        ln -s $TARGET $LINK

        mkdir -p $D$systemd_system_unitdir/obmc-host-stop@${i}.target.wants
        LINK="$D$systemd_system_unitdir/obmc-host-stop@${i}.target.wants/phosphor-reset-sensor-states@${i}.service"
        TARGET="../phosphor-reset-sensor-states@.service"
        ln -s $TARGET $LINK

        mkdir -p $D$systemd_system_unitdir/obmc-host-quiesce@${i}.target.wants
        LINK="$D$systemd_system_unitdir/obmc-host-quiesce@${i}.target.wants/phosphor-reset-sensor-states@${i}.service"
        TARGET="../phosphor-reset-sensor-states@.service"
        ln -s $TARGET $LINK

        mkdir -p $D$systemd_system_unitdir/obmc-host-shutdown@${i}.target.requires
        LINK="$D$systemd_system_unitdir/obmc-host-shutdown@${i}.target.requires/obmc-chassis-poweroff@${i}.target"
        TARGET="../obmc-chassis-poweroff@.target"
        ln -s $TARGET $LINK

        mkdir -p $D$systemd_system_unitdir/obmc-host-reboot@${i}.target.requires
        LINK="$D$systemd_system_unitdir/obmc-host-reboot@${i}.target.requires/obmc-host-shutdown@${i}.target"
        TARGET="../obmc-host-shutdown@.target"
        ln -s $TARGET $LINK

        LINK="$D$systemd_system_unitdir/obmc-host-reboot@${i}.target.requires/phosphor-reboot-host@${i}.service"
        TARGET="../phosphor-reboot-host@.service"
        ln -s $TARGET $LINK

        mkdir -p $D$systemd_system_unitdir/obmc-host-warm-reboot@${i}.target.requires
        LINK="$D$systemd_system_unitdir/obmc-host-warm-reboot@${i}.target.requires/xyz.openbmc_project.Ipmi.Internal.SoftPowerOff.service"
        TARGET="../xyz.openbmc_project.Ipmi.Internal.SoftPowerOff.service"
        ln -s $TARGET $LINK

        mkdir -p $D$systemd_system_unitdir/obmc-host-force-warm-reboot@${i}.target.requires
        LINK="$D$systemd_system_unitdir/obmc-host-force-warm-reboot@${i}.target.requires/obmc-host-stop@${i}.target"
        TARGET="../obmc-host-stop@.target"
        ln -s $TARGET $LINK

        LINK="$D$systemd_system_unitdir/obmc-host-force-warm-reboot@${i}.target.requires/phosphor-reboot-host@${i}.service"
        TARGET="../phosphor-reboot-host@.service"
        ln -s $TARGET $LINK

        LINK="$D$systemd_system_unitdir/obmc-host-warm-reboot@${i}.target.requires/obmc-host-force-warm-reboot@${i}.target"
        TARGET="../obmc-host-force-warm-reboot@.target"
        ln -s $TARGET $LINK

        mkdir -p $D$systemd_system_unitdir/obmc-chassis-poweroff@${i}.target.wants
        LINK="$D$systemd_system_unitdir/obmc-chassis-poweroff@${i}.target.wants/phosphor-clear-one-time@${i}.service"
        TARGET="../phosphor-clear-one-time@.service"
        ln -s $TARGET $LINK
    done
}

pkg_postinst:${PN}-chassis-check-power-status:append() {

    for i in ${OBMC_HOST_INSTANCES};
    do
        mkdir -p $D$systemd_system_unitdir/obmc-chassis-poweron@${i}.target.requires
        LINK="$D$systemd_system_unitdir/obmc-chassis-poweron@${i}.target.requires/phosphor-chassis-check-power-status@${i}.service"
        TARGET="../phosphor-chassis-check-power-status@.service"
        ln -s $TARGET $LINK
    done
}

pkg_prerm:${PN}-obmc-targets:append() {

    for i in ${OBMC_HOST_INSTANCES};
    do

        LINK="$D$systemd_system_unitdir/multi-user.target.requires/xyz.openbmc_project.State.Host@${i}.service"
        rm $LINK

        LINK="$D$systemd_system_unitdir/multi-user.target.requires/xyz.openbmc_project.State.Chassis@${i}.service"
        rm $LINK

        LINK="$D$systemd_system_unitdir/multi-user.target.requires/obmc-host-reset@${i}.target"
        rm $LINK

        LINK="$D$systemd_system_unitdir/multi-user.target.requires/phosphor-discover-system-state@${i}.service"
        rm $LINK

        LINK="$D$systemd_system_unitdir/obmc-chassis-poweron@${i}.target.wants/phosphor-reset-host-recovery@${i}.service"
        rm $LINK

        LINK="$D$systemd_system_unitdir/obmc-host-start@${i}.target.requires/obmc-host-startmin@${i}.target"
        rm $LINK

        LINK="$D$systemd_system_unitdir/obmc-host-start@${i}.target.requires/phosphor-reset-host-reboot-attempts@${i}.service"
        rm $LINK

        LINK="$D$systemd_system_unitdir/obmc-host-startmin@${i}.target.wants/phosphor-set-host-transition-to-running@${i}.service"
        rm $LINK

        LINK="$D$systemd_system_unitdir/obmc-host-stop@${i}.target.wants/phosphor-set-host-transition-to-off@${i}.service"
        rm $LINK

        LINK="$D$systemd_system_unitdir/obmc-host-stop@${i}.target.wants/phosphor-reset-sensor-states@${i}.service"
        rm $LINK

        LINK="$D$systemd_system_unitdir/obmc-host-quiesce@${i}.target.wants/phosphor-reset-sensor-states@${i}.service"
        rm $LINK

        LINK="$D$systemd_system_unitdir/obmc-host-reset@${i}.target.requires/phosphor-reset-host-running@${i}.service"
        rm $LINK

        LINK="$D$systemd_system_unitdir/obmc-host-shutdown@${i}.target.requires/obmc-chassis-poweroff@${i}.target"
        rm $LINK

        LINK="$D$systemd_system_unitdir/obmc-host-reboot@${i}.target.requires/obmc-host-shutdown@${i}.target"
        rm $LINK

        LINK="$D$systemd_system_unitdir/obmc-host-reboot@${i}.target.requires/phosphor-reboot-host@${i}.service"
        rm $LINK

        LINK="$D$systemd_system_unitdir/obmc-host-warm-reboot@${i}.target.requires/xyz.openbmc_project.Ipmi.Internal.SoftPowerOff.service"
        rm $LINK

        LINK="$D$systemd_system_unitdir/obmc-host-force-warm-reboot@${i}.target.requires/obmc-host-stop@${i}.target"
        rm $LINK

        LINK="$D$systemd_system_unitdir/obmc-host-force-warm-reboot@${i}.target.requires/phosphor-reboot-host@${i}.service"
        rm $LINK

        LINK="$D$systemd_system_unitdir/obmc-host-warm-reboot@${i}.target.requires/obmc-host-force-warm-reboot@${i}.target"
        rm $LINK

        LINK="$D$systemd_system_unitdir/obmc-chassis-poweroff@${i}.target.wants/phosphor-clear-one-time@${i}.service"
        rm $LINK

    done
}

pkg_prerm:${PN}-chassis-check-power-status:append() {

    for i in ${OBMC_HOST_INSTANCES};
    do

        LINK="$D$systemd_system_unitdir/obmc-chassis-poweron@${i}.target.requires/phosphor-chassis-check-power-status@${i}.service"
        rm $LINK

    done
}
