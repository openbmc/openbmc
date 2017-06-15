do_install_append() {
        DEST=${D}${inventory_envdir}
        printf "\nEEPROM=/sys/devices/platform/ahb/ahb:apb/ahb:apb:i2c@1e78a000/1e78a400.i2c-bus/i2c-11/11-0051/eeprom" >> ${DEST}/inventory
}
