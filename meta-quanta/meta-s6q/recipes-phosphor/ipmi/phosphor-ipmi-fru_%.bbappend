FILESEXTRAPATHS:prepend:s6q := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd
DEPENDS:append:s6q = " s6q-yaml-config"

EEPROMS_NAME_LIST = "bmc motherboard"

EEPROMFMT = "system/chassis/{0}"
EEPROM_ESCAPEDFMT = "system-chassis-{0}"
EEPROMS = "${@compose_list(d, 'EEPROMFMT', 'EEPROMS_NAME_LIST')}"
EEPROMS_ESCAPED = "${@compose_list(d, 'EEPROM_ESCAPEDFMT', 'EEPROMS_NAME_LIST')}"

ENVFMT = "obmc/eeproms/{0}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:s6q := " ${@compose_list(d, 'ENVFMT', 'EEPROMS')}"

TMPL = "obmc-read-eeprom@.service"
TGT = "multi-user.target"
INSTFMT = "obmc-read-eeprom@{0}.service"
FMT = "../${TMPL}:${TGT}.wants/${INSTFMT}"

SYSTEMD_LINK:${PN}:append:s6q := " ${@compose_list(d, 'FMT', 'EEPROMS_ESCAPED')}"

IPMI_FRU_YAML:s6q="${STAGING_DIR_HOST}${datadir}/s6q-yaml-config/ipmi-fru-read.yaml"
IPMI_FRU_PROP_YAML:s6q="${STAGING_DIR_HOST}${datadir}/s6q-yaml-config/ipmi-extra-properties.yaml"
