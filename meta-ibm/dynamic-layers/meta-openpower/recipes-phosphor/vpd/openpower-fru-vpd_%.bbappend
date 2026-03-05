FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SYSTEMD_SERVICE:${PN}:append:ibm-enterprise = " system-vpd.service"
SYSTEMD_SERVICE:${PN}:append:ibm-enterprise = " wait-vpd-parsers.service"
SYSTEMD_SERVICE:${PN}:append:ibm-enterprise = " vpd-manager.service"
PACKAGECONFIG:append:ibm-enterprise = " ibm_system"

FILES:${PN}:append:ibm-enterprise = " ${datadir}/vpd/*.json"

SRC_URI:append:mf-redundant-bmc = " file://vpd-manager-rbmc-override.conf"
FILES:${PN}:append:mf-redundant-bmc = " ${systemd_system_unitdir}/vpd-manager.service.d/vpd-manager-rbmc-override.conf"

do_install:append:ibm-enterprise() {
        # Remove files that are used by openpower-read-vpd
        DEST=${D}${inventory_envdir}
        rm ${DEST}/inventory
        rm ${D}/${nonarch_base_libdir}/udev/rules.d/70-op-vpd.rules
}

do_install:append:mf-redundant-bmc() {
        # patch the vpd-manager.service file
        override_dir=${D}${systemd_system_unitdir}/vpd-manager.service.d
        install -d ${override_dir}
        install -m 0644 ${UNPACKDIR}/vpd-manager-rbmc-override.conf \
                ${override_dir}/vpd-manager-rbmc-override.conf
}

do_install:append:witherspoon() {
        DEST=${D}${inventory_envdir}
        printf "\nEEPROM=/sys/devices/platform/ahb/ahb:apb/ahb:apb:bus@1e78a000/1e78a400.i2c/i2c-11/11-0051/eeprom" >> ${DEST}/inventory
}

pkg_postinst:${PN}:ibm-enterprise() {
    mkdir -p $D$systemd_system_unitdir/obmc-chassis-poweroff@0.target.wants
    LINK="$D$systemd_system_unitdir/obmc-chassis-poweroff@0.target.wants/wait-vpd-parsers.service"
    TARGET="../wait-vpd-parsers.service"
    ln -s $TARGET $LINK
}
pkg_prerm:${PN}:ibm-enterprise() {
    LINK="$D$systemd_system_unitdir/obmc-chassis-poweroff@0.target.wants/wait-vpd-parsers.service"
    rm $LINK
}
