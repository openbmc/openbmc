FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SYSTEMD_SERVICE_${PN}-ledmanager_append_rainier += "obmc-led-set-all-groups-asserted@.service"

# Copies config file having arguments for led-set-all-groups-asserted.sh
SYSTEMD_ENVIRONMENT_FILE_${PN}-ledmanager_append_rainier +="obmc/led/set-all/groups/config"

pkg_postinst_${PN}-ledmanager_rainier () {

    # Needed this to run as part of BMC boot
    mkdir -p $D$systemd_system_unitdir/multi-user.target.wants
    LINK="$D$systemd_system_unitdir/multi-user.target.wants/obmc-led-set-all-groups-asserted@false.service"
    TARGET="../obmc-led-set-all-groups-asserted@.service"
    ln -s $TARGET $LINK

    # Needed this to run as part of Power On
    mkdir -p $D$systemd_system_unitdir/obmc-chassis-poweron@0.target.wants
    LINK="$D$systemd_system_unitdir/obmc-chassis-poweron@0.target.wants/obmc-led-set-all-groups-asserted@false.service"
    TARGET="../obmc-led-set-all-groups-asserted@.service"
    ln -s $TARGET $LINK
}

pkg_prerm_${PN}-ledmanager_rainier () {

    LINK="$D$systemd_system_unitdir/multi-user.target.wants/obmc-led-set-all-groups-asserted@false.service"
    rm $LINK

    LINK="$D$systemd_system_unitdir/obmc-chassis-poweron@0.target.wants/obmc-led-set-all-groups-asserted@false.service"
    rm $LINK
}
