inherit entity-utils

FILESEXTRAPATHS:append:olympus-nuvoton := "${THISDIR}/${PN}:"

SRC_URI:append:olympus-nuvoton = " \
    file://0001-Add-set-BIOS-version-support.patch \
    file://0002-Add-support-for-enabling-disabling-network-IPMI.patch \
    file://0001-Fix-firmware-version-missing-at-dev-tag.patch \
    "

DEPENDS:append:olympus-nuvoton = " \
    ${@entity_enabled(d, '', 'olympus-nuvoton-yaml-config')}"

EXTRA_OEMESON:append:olympus-nuvoton = " \
    -Dboot-flag-safe-mode-support=enabled \
    -Dsensor-yaml-gen=${@entity_enabled(d, '', '${STAGING_DIR_HOST}${datadir}/olympus-nuvoton-yaml-config/ipmi-sensors.yaml')} \
    -Dfru-yaml-gen=${@entity_enabled(d, '', '${STAGING_DIR_HOST}${datadir}/olympus-nuvoton-yaml-config/ipmi-fru-read.yaml')} \
    -Dinvsensor-yaml-gen=${@entity_enabled(d, '', '${STAGING_DIR_HOST}${datadir}/olympus-nuvoton-yaml-config/ipmi-inventory-sensors.yaml')} \
    "
PACKAGECONFIG:append:olympus-entity = " dynamic-sensors"

# for intel ipmi oem
do_install:append:olympus-nuvoton(){
    install -d ${D}${includedir}/phosphor-ipmi-host
    install -m 0644 -D ${S}/sensorhandler.hpp ${D}${includedir}/phosphor-ipmi-host
    install -m 0644 -D ${S}/selutility.hpp ${D}${includedir}/phosphor-ipmi-host
}
