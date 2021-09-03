# This include file is aim to pass variables to bb/bb.apppend files,
# such as sysguid tool, and easily maintain platform settings


##################### Configure default EEPROM setting #####################
### The I2C bus is "0 based, decimal"
### The address and offset are "hexcimal"
### e.g.
###     EEPROM_GUID_I2C_BUS = "3"
###     EEPROM_GUID_I2C_ADDRESS = "0x54"
###     EEPROM_GUID_OFFSET = "0x1F00"

EEPROM_GUID_I2C_BUS= "3"
EEPROM_GUID_I2C_ADDRESS = "0x54"
EEPROM_GUID_OFFSET = "0x1F00"

EEPROM_MAC_I2C_BUS = "14"
EEPROM_MAC_I2C_ADDRESS = "0x50"
EEPROM_MAC_OFFSET = "0x1000"

############################################################################
