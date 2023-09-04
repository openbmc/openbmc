inherit obmc-phosphor-systemd

FILESEXTRAPATHS:prepend:sbp1 := "${THISDIR}/${PN}:"

DEPENDS:append:sbp1 = " sbp1-yaml-config"

IPMI_FRU_YAML:sbp1="${STAGING_DIR_HOST}${datadir}/sbp1-yaml-config/ipmi-fru-read.yaml"
IPMI_FRU_PROP_YAML:sbp1="${STAGING_DIR_HOST}${datadir}/sbp1-yaml-config/ipmi-extra-properties.yaml"

EEPROM_NAMES = "motherboard"

EEPROMFMT = "system/chassis/{0}"
EEPROM_ESCAPEDFMT = "system-chassis-{0}"
EEPROMS = "${@compose_list(d, 'EEPROMFMT', 'EEPROM_NAMES')}"
EEPROMS_ESCAPED = "${@compose_list(d, 'EEPROM_ESCAPEDFMT', 'EEPROM_NAMES')}"

ENVFMT = "obmc/eeproms/{0}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:sbp1 := " ${@compose_list(d, 'ENVFMT', 'EEPROMS')}"

TMPL = "obmc-read-eeprom@.service"
TGT = "${SYSTEMD_DEFAULT_TARGET}"
INSTFMT = "obmc-read-eeprom@{0}.service"
FMT = "../${TMPL}:${TGT}.wants/${INSTFMT}"

SYSTEMD_LINK:${PN}:append:sbp1 := " ${@compose_list(d, 'FMT', 'EEPROMS_ESCAPED')}"
