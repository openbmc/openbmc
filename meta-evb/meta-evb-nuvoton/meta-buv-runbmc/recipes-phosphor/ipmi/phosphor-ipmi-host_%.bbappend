inherit buv-entity-utils

FILESEXTRAPATHS:append:buv-runbmc := "${THISDIR}/${PN}:"
SRC_URI:append:buv-runbmc = " file://0001-Add-set-BIOS-version-support.patch"
SRC_URI:append:buv-runbmc = " file://0003-Add-option-for-SEL-commands-for-Journal-based-SEL-en.patch"
SRC_URI:append:buv-runbmc = " file://0001-Fix-firmware-version-missing-at-dev-tag.patch"


DEPENDS:append:buv-runbmc= " \
    ${@entity_enabled(d, '', ' buv-runbmc-yaml-config')}"

EXTRA_OECONF:buv-runbmc = " \
    --enable-boot-flag-safe-mode-support \
    --with-journal-sel \
    ${@entity_enabled(d, '', ' SENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/buv-runbmc-yaml-config/ipmi-sensors.yaml')} \
    ${@entity_enabled(d, '', ' FRU_YAML_GEN=${STAGING_DIR_HOST}${datadir}/buv-runbmc-yaml-config/ipmi-fru-read.yaml')} \
    ${@entity_enabled(d, '', ' INVSENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/buv-runbmc-yaml-config/ipmi-inventory-sensors.yaml')} \
    ${@entity_enabled(d, '', ' --disable-dynamic_sensors')} \
    "

do_install:append:buv-entity(){
    install -d ${D}${includedir}/phosphor-ipmi-host
    install -m 0644 -D ${S}/sensorhandler.hpp ${D}${includedir}/phosphor-ipmi-host
    install -m 0644 -D ${S}/selutility.hpp ${D}${includedir}/phosphor-ipmi-host
}
