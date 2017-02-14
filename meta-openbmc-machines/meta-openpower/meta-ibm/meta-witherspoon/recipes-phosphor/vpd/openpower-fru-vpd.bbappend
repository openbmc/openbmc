do_install_append() {
        DEST=${D}${inventory_envdir}
        printf "\nEEPROM=/sys/devices/platform/ahb/ahb:apb/1e78a000.i2c/i2c-11/i2c-11/11-0051/eeprom" >> ${DEST}/inventory
}
