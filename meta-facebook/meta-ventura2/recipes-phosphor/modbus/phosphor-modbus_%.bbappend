# Set the systemd auto-enable variable to 'disable' for this package as rackmon is the
# service handling modbus communication at the moment.
SYSTEMD_AUTO_ENABLE:${PN} = "disable"
