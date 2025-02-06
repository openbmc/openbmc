DEPENDS:append:p10bmc = " cli11"
DEPENDS:append:p10bmc = " nlohmann-json"
DEPENDS:append:p10bmc = " phosphor-dbus-interfaces"
DEPENDS:append:p10bmc = " libgpiod"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SYSTEMD_SERVICE:${PN}:append:p10bmc = " system-vpd.service"
SYSTEMD_SERVICE:${PN}:append:p10bmc = " wait-vpd-parsers.service"
SYSTEMD_SERVICE:${PN}:append:p10bmc = " vpd-manager.service"
PACKAGECONFIG:append:p10bmc = " ibm_system"

FILES:${PN}:append:p10bmc = " ${datadir}/vpd/*.json"

do_install:append:p10bmc() {
        # Remove files that are used by openpower-read-vpd
        DEST=${D}${inventory_envdir}
        rm ${DEST}/inventory
        rm ${D}/${nonarch_base_libdir}/udev/rules.d/70-op-vpd.rules
}

do_install:append:witherspoon() {
        DEST=${D}${inventory_envdir}
        printf "\nEEPROM=/sys/devices/platform/ahb/1e780000.apb/1e780000.apb:bus@1e78a000/1e78a400.i2c/i2c-11/11-0051/eeprom" >> ${DEST}/inventory
}

do_install:append:witherspoon-tacoma() {
        DEST=${D}${inventory_envdir}
        printf "FRUS=BMC,ETHERNET" > ${DEST}/inventory
        printf "\nPATHS=/system/chassis/motherboard/bmc,/system/chassis/motherboard/bmc/eth0" >> ${DEST}/inventory
        printf "\nEEPROM=/sys/devices/platform/ahb/1e780000.apb/1e780000.apb:bus@1e78a000/1e78a600.i2c/i2c-11/11-0051/eeprom" >> ${DEST}/inventory
}

pkg_postinst:${PN}:p10bmc() {
    mkdir -p $D$systemd_system_unitdir/obmc-chassis-poweroff@0.target.wants
    LINK="$D$systemd_system_unitdir/obmc-chassis-poweroff@0.target.wants/wait-vpd-parsers.service"
    TARGET="../wait-vpd-parsers.service"
    ln -s $TARGET $LINK
}
pkg_prerm:${PN}:p10bmc() {
    LINK="$D$systemd_system_unitdir/obmc-chassis-poweroff@0.target.wants/wait-vpd-parsers.service"
    rm $LINK
}