FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

EEPROM_NAMES = "io_board motherboard sas_expander hdd_backplane"
EEPROMS = "${@compose_list_zip(d, 'system/chassis/[0]', 'EEPROM_NAMES')}"
EEPROMS_ESCAPED = "${@compose_list_zip(d, 'system-chassis-[0]', 'EEPROM_NAMES')}"

SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list_zip(d, 'obmc/eeproms/[0]', 'EEPROMS')}"
SYSTEMD_GENLINKS_${PN} += "../obmc-read-eeprom@.service:${SYSTEMD_DEFAULT_TARGET}.wants/obmc-read-eeprom@[0].service:EEPROMS_ESCAPED"
