inherit obmc-phosphor-systemd

FILESEXTRAPATHS:prepend:evb-npcm750 := "${THISDIR}/${PN}:"

DEPENDS:append:evb-npcm750 = " \
    evb-npcm750-yaml-config"


EXTRA_OECONF:evb-npcm750 = " \
    YAML_GEN=${STAGING_DIR_HOST}${datadir}/evb-npcm750-yaml-config/ipmi-fru-read.yaml \
    PROP_YAML=${STAGING_DIR_HOST}${datadir}/evb-npcm750-yaml-config/ipmi-extra-properties.yaml \
    "

EEPROM_NAMES = "bmc"

EEPROMFMT = "system/chassis/{0}"
EEPROM_ESCAPEDFMT = "system-chassis-{0}"
EEPROMS = "${@compose_list(d, 'EEPROMFMT', 'EEPROM_NAMES')}"
EEPROMS_ESCAPED = "${@compose_list(d, 'EEPROM_ESCAPEDFMT', 'EEPROM_NAMES')}"

ENVFMT = "obmc/eeproms/{0}"
ENVF = "${@compose_list(d, 'ENVFMT', 'EEPROMS')}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append_evb-npcm750 := " ${ENVF}"

TMPL = "obmc-read-eeprom@.service"
TGT = "${SYSTEMD_DEFAULT_TARGET}"
INSTFMT = "obmc-read-eeprom@{0}.service"
FMT = "../${TMPL}:${TGT}.wants/${INSTFMT}"
LINKS = "${@compose_list(d, 'FMT', 'EEPROMS_ESCAPED')}"
SYSTEMD_LINK:${PN}:append:evb-npcm750 := "${LINKS}"
