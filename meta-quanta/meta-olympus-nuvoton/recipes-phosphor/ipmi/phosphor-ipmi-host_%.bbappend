inherit entity-utils

FILESEXTRAPATHS:append:olympus-nuvoton := "${THISDIR}/${PN}:"

SRC_URI:append:olympus-nuvoton = " \
    file://0001-Add-set-BIOS-version-support.patch \
    file://0002-Add-support-for-enabling-disabling-network-IPMI.patch \
    file://0003-Add-option-for-SEL-commands-for-Journal-based-SEL-en.patch \
    file://0001-Fix-firmware-version-missing-at-dev-tag.patch \
    "

DEPENDS:append:olympus-nuvoton = " \
    ${@entity_enabled(d, '', 'olympus-nuvoton-yaml-config')}"

EXTRA_OECONF:append:olympus-nuvoton  = " --with-journal-sel"

EXTRA_OECONF:append:olympus-nuvoton = " \
    --enable-boot-flag-safe-mode-support \
    ${@entity_enabled(d, '', 'SENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/olympus-nuvoton-yaml-config/ipmi-sensors.yaml')} \
    ${@entity_enabled(d, '', 'FRU_YAML_GEN=${STAGING_DIR_HOST}${datadir}/olympus-nuvoton-yaml-config/ipmi-fru-read.yaml')} \
    ${@entity_enabled(d, '', 'INVSENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/olympus-nuvoton-yaml-config/ipmi-inventory-sensors.yaml')} \
    "
PACKAGECONFIG:append:olympus-entity = " dynamic-sensors"

# for intel ipmi oem
do_install:append:olympus-nuvoton(){
    install -d ${D}${includedir}/phosphor-ipmi-host
    install -m 0644 -D ${S}/sensorhandler.hpp ${D}${includedir}/phosphor-ipmi-host
    install -m 0644 -D ${S}/selutility.hpp ${D}${includedir}/phosphor-ipmi-host
}
