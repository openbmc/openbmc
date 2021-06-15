inherit obmc-phosphor-systemd
DEPENDS_append_gbs = " gbs-yaml-config"

FILESEXTRAPATHS_prepend_gbs := "${THISDIR}/${PN}:"

EEPROM_NAMES = "motherboard hsbp fan"

EEPROMFMT = "system/chassis/{0}"
EEPROM_ESCAPEDFMT = "system-chassis-{0}"
EEPROMS = "${@compose_list(d, 'EEPROMFMT', 'EEPROM_NAMES')}"
EEPROMS_ESCAPED = "${@compose_list(d, 'EEPROM_ESCAPEDFMT', 'EEPROM_NAMES')}"

ENVFMT = "obmc/eeproms/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN}_append_gbs := " ${@compose_list(d, 'ENVFMT', 'EEPROMS')}"

TMPL = "obmc-read-eeprom@.service"
TGT = "multi-user.target"
INSTFMT = "obmc-read-eeprom@{0}.service"
FMT = "../${TMPL}:${TGT}.wants/${INSTFMT}"

SYSTEMD_LINK_${PN}_append_gbs := " ${@compose_list(d, 'FMT', 'EEPROMS_ESCAPED')}"

EXTRA_OECONF_append_gbs = " \
    YAML_GEN=${STAGING_DIR_HOST}${datadir}/gbs-yaml-config/ipmi-fru-read.yaml \
    PROP_YAML=${STAGING_DIR_HOST}${datadir}/gbs-yaml-config/ipmi-extra-properties.yaml \
    "
