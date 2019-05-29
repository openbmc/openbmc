do_install_append_witherspoon() {
        DEST=${D}${inventory_envdir}
        printf "\nEEPROM=/sys/devices/platform/ahb/ahb:apb/ahb:apb:bus@1e78a000/1e78a400.i2c-bus/i2c-11/11-0051/eeprom" >> ${DEST}/inventory
}

do_install_append_swift() {
        DEST=${D}${inventory_envdir}
        printf "\nEEPROM=/sys/devices/platform/ahb/ahb:apb/ahb:apb:bus@1e78a000/1e78a340.i2c-bus/i2c-8/8-0051/eeprom" >> ${DEST}/inventory
}
