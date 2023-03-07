RDEPENDS:${PN}-monitor = " \
    ${@entity_enabled(d, 'phosphor-power-monitor-em', 'phosphor-power-monitor')} \
    "

pkg_prerm:${PN}() {
        [ -z "${OBMC_POWER_SUPPLY_INSTANCES}" ] && echo "No power supply instance defined" && exit 1
        for inst in ${OBMC_POWER_SUPPLY_INSTANCES}; do
                if [ "${DISTRO}" != "buv-entity" ];then
                        LINK="$D$systemd_system_unitdir/multi-user.target.requires/power-supply-monitor@$inst.service"
                        rm $LINK
                else
                        LINK="$D$systemd_system_unitdir/multi-user.target.requires/power-supply-monitor-em@$inst.service"
                        rm $LINK
                fi
        done
}
pkg_postinst:${PN}() {
        mkdir -p $D$systemd_system_unitdir/multi-user.target.requires
        [ -z "${OBMC_POWER_SUPPLY_INSTANCES}" ] && echo "No power supply instance defined" && exit 1
        for inst in ${OBMC_POWER_SUPPLY_INSTANCES}; do
                if [ "${DISTRO}" != "buv-entity" ];then
                        LINK="$D$systemd_system_unitdir/multi-user.target.requires/power-supply-monitor@$inst.service"
                        TARGET="../power-supply-monitor@.service"
                        ln -s $TARGET $LINK
                else
                        LINK="$D$systemd_system_unitdir/multi-user.target.requires/power-supply-monitor-em@$inst.service"
                        TARGET="../power-supply-monitor-em@.service"
                        ln -s $TARGET $LINK
                fi
        done
}
