inherit entity-utils

FILESEXTRAPATHS:append:evb-npcm845 := "${THISDIR}/${PN}:"

DEPENDS:append:evb-npcm845 = " ${@entity_enabled(d, '', ' evb-npcm845-yaml-config')}"

EXTRA_OEMESON:append:evb-npcm845 = " \
    -Di2c-whitelist-check=disabled \
    -Dsel-logger-clears-sel=enabled \
    -Dsensor-yaml-gen=${@entity_enabled(d, '', '${STAGING_DIR_HOST}${datadir}/evb-npcm845-yaml-config/ipmi-sensors.yaml')} \
    -Dfru-yaml-gen=${@entity_enabled(d, '', '${STAGING_DIR_HOST}${datadir}/evb-npcm845-yaml-config/ipmi-fru-read.yaml')} \
    -Dinvsensor-yaml-gen=${@entity_enabled(d, '', '${STAGING_DIR_HOST}${datadir}/evb-npcm845-yaml-config/ipmi-inventory-sensors.yaml')} \
    "

# Add send/get message support
# ipmid <-> ipmb <-> i2c
SRC_URI:append:evb-npcm845 = " file://0002-Support-bridging-commands.patch"

# Get sel events from journal logs, the build opetion should with "--with-journal-sel"
# EXTRA_OECONF:append:evb-npcm845 = " --with-journal-sel"
# SRC_URI:append:evb-npcm845 = " file://0004-Add-option-for-SEL-commands-for-Journal-based-SEL-en.patch"

PACKAGECONFIG:append:evb-npcm845 = " ${@entity_enabled(d, 'dynamic-sensors', '')}"

# avoid build error after remove ipmi-fru
WHITELIST_CONF:evb-npcm845 = "${S}/host-ipmid-whitelist.conf"
