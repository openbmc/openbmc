inherit obmc-phosphor-systemd

DEPENDS:append:ncplite = " ncplite-yaml-config"

FILESEXTRAPATHS:prepend:ncplite := "${THISDIR}/${PN}:"

SRC_URI:append:ncplite = " file://ncplite-obmc-read-eeprom@.service \
                         "


IPMI_FRU_YAML:ncplite="${STAGING_DIR_HOST}${datadir}/ncplite-yaml-config/ipmi-fru-read.yaml"
IPMI_FRU_PROP_YAML:ncplite="${STAGING_DIR_HOST}${datadir}/ncplite-yaml-config/ipmi-extra-properties.yaml"

EEPROM_NAMES = "motherboard psu1 psu0"

EEPROMFMT = "system/chassis/{0}"
EEPROM_ESCAPEDFMT = "system-chassis-{0}"
EEPROMS = "${@compose_list(d, 'EEPROMFMT', 'EEPROM_NAMES')}"
EEPROMS_ESCAPED = "${@compose_list(d, 'EEPROM_ESCAPEDFMT', 'EEPROM_NAMES')}"

ENVFMT = "obmc/eeproms/{0}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:ncplite := " ${@compose_list(d, 'ENVFMT', 'EEPROMS')}"

TMPL = "obmc-read-eeprom@.service"
TGT = "multi-user.target"
INSTFMT = "obmc-read-eeprom@{0}.service"
FMT = "../${TMPL}:${TGT}.wants/${INSTFMT}"

SYSTEMD_LINK:${PN}:append:ncplite := " ${@compose_list(d, 'FMT', 'EEPROMS_ESCAPED')}"

do_install:append:ncplite() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/ncplite-obmc-read-eeprom@.service ${D}${systemd_system_unitdir}/obmc-read-eeprom@.service
}
