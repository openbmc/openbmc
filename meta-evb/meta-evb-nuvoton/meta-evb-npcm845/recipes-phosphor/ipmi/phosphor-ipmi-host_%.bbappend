inherit entity-utils

FILESEXTRAPATHS:append:evb-npcm845 := "${THISDIR}/${PN}:"

DEPENDS:append:evb-npcm845 = " ${@entity_enabled(d, '', ' evb-npcm845-yaml-config')}"

EXTRA_OEMESON:append:evb-npcm845 = " \
    -Di2c-whitelist-check=disabled \
    -Dsel-logger-clears-sel=enabled \
    ${@entity_enabled(d, '', '-Dsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/evb-npcm845-yaml-config/ipmi-sensors.yaml')} \
    ${@entity_enabled(d, '', '-Dfru-yaml-gen=${STAGING_DIR_HOST}${datadir}/evb-npcm845-yaml-config/ipmi-fru-read.yaml')} \
    ${@entity_enabled(d, '', '-Dinvsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/evb-npcm845-yaml-config/ipmi-inventory-sensors.yaml')} \
    "

# Add send/get message support
# ipmid <-> ipmb <-> i2c
SRC_URI:append:evb-npcm845 = " file://0002-Support-bridging-commands.patch"

# Fix build error when enable sel-logger-clears-sel
# EXTRA_OEMESON:append:evb-npcm845 = "-Dsel-logger-clears-sel=enabled"
#SRC_URI:append:evb-npcm845 = " file://0008-dbus-sdr-fix-build-error-when-enable-sel-logger-clea.patch"

# Fix build error when enable dbus-sdr
# SRC_URI:append:evb-npcm845 = " file://0001-dbus-sdr-fix-build-break.patch"

PACKAGECONFIG:append:evb-npcm845 = " ${@entity_enabled(d, 'dynamic-sensors', '')}"

# avoid build error after remove ipmi-fru
WHITELIST_CONF:evb-npcm845 = "${S}/host-ipmid-whitelist.conf"
