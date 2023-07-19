inherit obmc-phosphor-systemd

FILESEXTRAPATHS:prepend:bletchley := "${THISDIR}/${PN}:"

DEPENDS:append:bletchley = " bletchley-yaml-config"

IPMI_FRU_YAML:bletchley = "${STAGING_DIR_HOST}${datadir}/bletchley-yaml-config/ipmi-fru-read.yaml"

EEPROM_NAMES = "bmc"

EEPROMFMT = "system/chassis/{0}"
EEPROM_ESCAPEDFMT = "system-chassis-{0}"
EEPROMS = "${@compose_list(d, 'EEPROMFMT', 'EEPROM_NAMES')}"
EEPROMS_ESCAPED = "${@compose_list(d, 'EEPROM_ESCAPEDFMT', 'EEPROM_NAMES')}"

ENVFMT = "obmc/eeproms/{0}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:bletchley := " ${@compose_list(d, 'ENVFMT', 'EEPROMS')}"

TMPL = "obmc-read-eeprom@.service"
TGT = "${SYSTEMD_DEFAULT_TARGET}"
INSTFMT = "obmc-read-eeprom@{0}.service"
FMT = "../${TMPL}:${TGT}.wants/${INSTFMT}"

SYSTEMD_LINK:${PN}:append:bletchley := " ${@compose_list(d, 'FMT', 'EEPROMS_ESCAPED')}"
