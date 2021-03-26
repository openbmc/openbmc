FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SYSTEMD_SERVICE_${PN}_append_rainier += "obmc-led-set-all-groups-asserted@.service"

# Copies config file having arguments for led-set-all-groups-asserted.sh
SYSTEMD_ENVIRONMENT_FILE_${PN}_append_rainier +="obmc/led/set-all/groups/config"

# Use the JSON configuration file at runtime than the static led.yaml
# Also, enable Lamp Test feature for rainier systems
PACKAGECONFIG_append_rainier = " use-json use-lamp-test"

pkg_postinst_${PN}_rainier () {

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

pkg_prerm_${PN}_rainier () {

    LINK="$D$systemd_system_unitdir/multi-user.target.wants/obmc-led-set-all-groups-asserted@false.service"
    rm $LINK

    LINK="$D$systemd_system_unitdir/obmc-chassis-poweron@0.target.wants/obmc-led-set-all-groups-asserted@false.service"
    rm $LINK
}
