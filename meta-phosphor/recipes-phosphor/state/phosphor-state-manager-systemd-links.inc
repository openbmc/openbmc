HOST_DEFAULT_TARGETS = " \
    multi-user.target.requires/obmc-host-reset@{}.target \
    multi-user.target.wants/phosphor-discover-system-state@{}.service \
    obmc-host-start@{}.target.requires/obmc-host-startmin@{}.target \
    obmc-host-start@{}.target.requires/phosphor-reset-host-reboot-attempts@{}.service \
    obmc-host-startmin@{}.target.wants/phosphor-set-host-transition-to-running@{}.service \
    obmc-host-stop@{}.target.wants/phosphor-set-host-transition-to-off@{}.service \
    obmc-host-reset@{}.target.requires/phosphor-reset-host-running@{}.service \
    obmc-host-stop@{}.target.wants/phosphor-reset-sensor-states@{}.service \
    obmc-host-graceful-quiesce@{}.target.wants/obmc-host-quiesce@{}.target \
    obmc-host-graceful-quiesce@{}.target.wants/pldmSoftPowerOff.service \
    obmc-host-quiesce@{}.target.wants/phosphor-reset-sensor-states@{}.service \
    obmc-host-reboot@{}.target.requires/obmc-host-shutdown@{}.target \
    obmc-host-reboot@{}.target.requires/phosphor-reboot-host@{}.service \
    obmc-host-warm-reboot@{}.target.requires/xyz.openbmc_project.Ipmi.Internal.SoftPowerOff.service \
    obmc-host-force-warm-reboot@{}.target.requires/obmc-host-stop@{}.target \
    obmc-host-force-warm-reboot@{}.target.requires/phosphor-reboot-host@{}.service \
    obmc-host-warm-reboot@{}.target.requires/obmc-host-force-warm-reboot@{}.target \
"

CHASSIS_DEFAULT_TARGETS = " \
    obmc-chassis-poweron@{}.target.wants/phosphor-reset-host-recovery@{}.service \
    obmc-chassis-poweron@{}.target.requires/obmc-power-start@{}.service \
    obmc-host-shutdown@{}.target.requires/obmc-chassis-poweroff@{}.target \
    obmc-chassis-poweroff@{}.target.requires/obmc-power-stop@{}.service \
    obmc-chassis-poweroff@{}.target.requires/obmc-powered-off@{}.service \
    obmc-chassis-poweroff@{}.target.wants/phosphor-clear-one-time@{}.service \
    obmc-chassis-blackout@{}.target.wants/phosphor-reset-sensor-states@{}.service \
    obmc-chassis-powerreset@{}.target.requires/phosphor-reset-chassis-on@{}.service \
    obmc-chassis-powerreset@{}.target.requires/phosphor-reset-chassis-running@{}.service \
"

# TODO: There are some targets that are chassis oriented, but there is no
#       obvious way to map HOST<->CHASSIS relationships.  Making an assumption
#       currently that there is a 1-to-1 relationship between the two.

pkg_postinst:${PN}-obmc-targets:append() {
    for i in ${OBMC_HOST_INSTANCES};
    do
        for j in ${HOST_DEFAULT_TARGETS};
        do
            LINK="$D$systemd_system_unitdir/${j}"
            LINK="$(echo ${LINK} | sed s/{}/${i}/g )"
            TARGET="../$(basename ${j} | sed s/{}//g )"
            mkdir -p "$(dirname ${LINK})"
            ln -s "${TARGET}" "${LINK}"
        done
    done

    for i in ${OBMC_CHASSIS_INSTANCES};
    do
        for j in ${CHASSIS_DEFAULT_TARGETS};
        do
            LINK="$D$systemd_system_unitdir/${j}"
            LINK="$(echo ${LINK} | sed s/{}/${i}/g )"
            TARGET="../$(basename ${j} | sed s/{}//g )"
            mkdir -p "$(dirname ${LINK})"
            ln -s "${TARGET}" "${LINK}"
        done
    done
}

pkg_prerm:${PN}-obmc-targets:append() {
    for i in ${OBMC_HOST_INSTANCES};
    do
        for j in ${HOST_DEFAULT_TARGETS};
        do
            LINK="$D$systemd_system_unitdir/${j}"
            LINK="$(echo ${LINK} | sed s/{}/${i}/g )"
            rm "${LINK}"
        done
    done

    for i in ${OBMC_CHASSIS_INSTANCES};
    do
        for j in ${CHASSIS_DEFAULT_TARGETS};
        do
            LINK="$D$systemd_system_unitdir/${j}"
            LINK="$(echo ${LINK} | sed s/{}/${i}/g )"
            rm "${LINK}"
        done
    done
}

pkg_postinst:${PN}-chassis:append() {
    for i in ${OBMC_CHASSIS_INSTANCES};
    do
        mkdir -p $D$systemd_system_unitdir/multi-user.target.requires
        LINK="$D$systemd_system_unitdir/multi-user.target.requires/xyz.openbmc_project.State.Chassis@${i}.service"
        TARGET="../xyz.openbmc_project.State.Chassis@.service"
        ln -s $TARGET $LINK
    done
}

pkg_prerm:${PN}-chassis:append() {
    for i in ${OBMC_CHASSIS_INSTANCES};
    do
        LINK="$D$systemd_system_unitdir/multi-user.target.requires/xyz.openbmc_project.State.Chassis@${i}.service"
        rm $LINK
    done
}

pkg_postinst:${PN}-chassis-check-power-status:append() {
    for i in ${OBMC_CHASSIS_INSTANCES};
    do
        mkdir -p $D$systemd_system_unitdir/obmc-chassis-poweron@${i}.target.requires
        LINK="$D$systemd_system_unitdir/obmc-chassis-poweron@${i}.target.requires/phosphor-chassis-check-power-status@${i}.service"
        TARGET="../phosphor-chassis-check-power-status@.service"
        ln -s $TARGET $LINK
    done
}

pkg_prerm:${PN}-chassis-check-power-status:append() {
    for i in ${OBMC_CHASSIS_INSTANCES};
    do
        LINK="$D$systemd_system_unitdir/obmc-chassis-poweron@${i}.target.requires/phosphor-chassis-check-power-status@${i}.service"
        rm $LINK
    done
}

pkg_postinst:${PN}-host:append() {
    for i in ${OBMC_HOST_INSTANCES};
    do
        mkdir -p $D$systemd_system_unitdir/multi-user.target.requires
        LINK="$D$systemd_system_unitdir/multi-user.target.requires/xyz.openbmc_project.State.Host@${i}.service"
        TARGET="../xyz.openbmc_project.State.Host@.service"
        ln -s $TARGET $LINK
    done
}

pkg_prerm:${PN}-host:append() {
    for i in ${OBMC_HOST_INSTANCES};
    do
        LINK="$D$systemd_system_unitdir/multi-user.target.requires/xyz.openbmc_project.State.Host@${i}.service"
        rm $LINK
    done
}

pkg_postinst:${PN}-scheduled-host-transition:append() {
    for i in ${OBMC_HOST_INSTANCES};
    do
        mkdir -p $D$systemd_system_unitdir/multi-user.target.wants
        LINK="$D$systemd_system_unitdir/multi-user.target.wants/xyz.openbmc_project.State.ScheduledHostTransition@${i}.service"
        TARGET="../xyz.openbmc_project.State.ScheduledHostTransition@.service"
        ln -s $TARGET $LINK
    done
}

pkg_prerm:${PN}-scheduled-host-transition:append() {
    for i in ${OBMC_HOST_INSTANCES};
    do
        LINK="$D$systemd_system_unitdir/multi-user.target.wants/xyz.openbmc_project.State.ScheduledHostTransition@${i}.service"
        rm $LINK
    done
}
