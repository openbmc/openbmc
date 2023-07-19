inherit obmc-phosphor-systemd
DEPENDS:append:gbs = " gbs-yaml-config"

FILESEXTRAPATHS:prepend:gbs := "${THISDIR}/${PN}:"

EEPROM_NAMES = "motherboard hsbp fan"

EEPROMFMT = "system/chassis/{0}"
EEPROM_ESCAPEDFMT = "system-chassis-{0}"
EEPROMS = "${@compose_list(d, 'EEPROMFMT', 'EEPROM_NAMES')}"
EEPROMS_ESCAPED = "${@compose_list(d, 'EEPROM_ESCAPEDFMT', 'EEPROM_NAMES')}"

ENVFMT = "obmc/eeproms/{0}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:gbs := " ${@compose_list(d, 'ENVFMT', 'EEPROMS')}"

TMPL = "obmc-read-eeprom@.service"
TGT = "multi-user.target"
INSTFMT = "obmc-read-eeprom@{0}.service"
FMT = "../${TMPL}:${TGT}.wants/${INSTFMT}"

SYSTEMD_LINK:${PN}:append:gbs := " ${@compose_list(d, 'FMT', 'EEPROMS_ESCAPED')}"

EXTRA_OECONF:append:gbs = ""

IPMI_FRU_YAML:gbs="${STAGING_DIR_HOST}${datadir}/gbs-yaml-config/ipmi-fru-read.yaml"
IPMI_FRU_PROP_YAML:gbs="${STAGING_DIR_HOST}${datadir}/gbs-yaml-config/ipmi-extra-properties.yaml"
