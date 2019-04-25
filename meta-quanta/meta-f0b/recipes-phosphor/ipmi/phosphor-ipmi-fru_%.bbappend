inherit obmc-phosphor-systemd

FILESEXTRAPATHS_prepend_f0b := "${THISDIR}/${PN}:"

EEPROM_NAMES = "motherboard "

EEPROMFMT = "system/{0}"
EEPROM_ESCAPEDFMT = "system-{0}"
EEPROMS = "${@compose_list(d, 'EEPROMFMT', 'EEPROM_NAMES')}"
EEPROMS_ESCAPED = "${@compose_list(d, 'EEPROM_ESCAPEDFMT', 'EEPROM_NAMES')}"

ENVFMT = "obmc/eeproms/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN}_append_f0b := " ${@compose_list(d, 'ENVFMT', 'EEPROMS')}"

TMPL = "obmc-read-eeprom@.service"
TGT = "multi-user.target"
INSTFMT = "obmc-read-eeprom@{0}.service"
FMT = "../${TMPL}:${TGT}.wants/${INSTFMT}"

SYSTEMD_LINK_${PN}_append_f0b := " ${@compose_list(d, 'FMT', 'EEPROMS_ESCAPED')}"
