FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SYSTEMD_SERVICE:${PN}:append:ibm-enterprise = " obmc-led-create-virtual-leds@.service"

# Copies config file having arguments for led-set-all-groups-asserted.sh
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:ibm-enterprise = " obmc/led/set-all/groups/config"

# Use the JSON configuration file at runtime than the static led.yaml
# Also, enable Lamp Test and OperationalStatus monitor feature for
# ibm-enterprise systems
PACKAGECONFIG:append:ibm-enterprise = " use-lamp-test monitor-operational-status persistent-led-asserted"

# Install the lamp test override file for ibm-enterprise
SRC_URI:append:ibm-enterprise = " file://lamp-test-led-overrides.json"

pkg_postinst:${PN}:ibm-enterprise () {

    # Needed this to run as part of BMC boot
    mkdir -p $D$systemd_system_unitdir/multi-user.target.wants
    LINK_FAULT="$D$systemd_system_unitdir/multi-user.target.wants/obmc-led-create-virtual-leds@sys-class-leds-virtual-enc-fault.service"
    TARGET_FAULT="../obmc-led-create-virtual-leds@.service"
    ln -s $TARGET_FAULT $LINK_FAULT

    # Needed this to run as part of BMC boot
    mkdir -p $D$systemd_system_unitdir/multi-user.target.wants
    LINK_ID="$D$systemd_system_unitdir/multi-user.target.wants/obmc-led-create-virtual-leds@sys-class-leds-virtual-enc-id.service"
    TARGET_ID="../obmc-led-create-virtual-leds@.service"
    ln -s $TARGET_ID $LINK_ID
}

pkg_prerm:${PN}:ibm-enterprise () {

    LINK_FAULT="$D$systemd_system_unitdir/multi-user.target.wants/obmc-led-create-virtual-leds@sys-class-leds-virtual-enc-fault.service"
    rm $LINK_FAULT

    LINK_ID="$D$systemd_system_unitdir/multi-user.target.wants/obmc-led-create-virtual-leds@sys-class-leds-virtual-enc-id.service"
    rm $LINK_ID
}

# Install lamp test override json
do_install:append:ibm-enterprise() {
    install -d ${D}${datadir}/${BPN}/
    install -m 0644 ${UNPACKDIR}/lamp-test-led-overrides.json ${D}${datadir}/${BPN}/
}
