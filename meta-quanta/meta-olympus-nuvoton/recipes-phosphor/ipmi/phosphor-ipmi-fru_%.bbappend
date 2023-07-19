inherit obmc-phosphor-systemd

FILESEXTRAPATHS:prepend:olympus-nuvoton := "${THISDIR}/${PN}:"

DEPENDS:append:olympus-nuvoton = " olympus-nuvoton-yaml-config"

EXTRA_OECONF:olympus-nuvoton = ""

IPMI_FRU_YAML:olympus-nuvoton="${STAGING_DIR_HOST}${datadir}/olympus-nuvoton-yaml-config/ipmi-fru-read.yaml"
IPMI_FRU_PROP_YAML:olympus-nuvoton="${STAGING_DIR_HOST}${datadir}/olympus-nuvoton-yaml-config/ipmi-extra-properties.yaml"

EEPROM_NAMES = "motherboard bmc"

EEPROMFMT = "system/chassis/{0}"
EEPROM_ESCAPEDFMT = "system-chassis-{0}"
EEPROMS = "${@compose_list(d, 'EEPROMFMT', 'EEPROM_NAMES')}"
EEPROMS_ESCAPED = "${@compose_list(d, 'EEPROM_ESCAPEDFMT', 'EEPROM_NAMES')}"

ENVFMT = "obmc/eeproms/{0}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:olympus-nuvoton := " ${@compose_list(d, 'ENVFMT', 'EEPROMS')}"

TMPL = "obmc-read-eeprom@.service"
TGT = "${SYSTEMD_DEFAULT_TARGET}"
INSTFMT = "obmc-read-eeprom@{0}.service"
FMT = "../${TMPL}:${TGT}.wants/${INSTFMT}"

SYSTEMD_LINK:${PN}:append:olympus-nuvoton := " ${@compose_list(d, 'FMT', 'EEPROMS_ESCAPED')}"
