FILESEXTRAPATHS:append:evb-npcm750 := "${THISDIR}/${PN}:"
SRC_URI:append:evb-npcm750 = " file://0001-Add-set-BIOS-version-support.patch"
SRC_URI:append:evb-npcm750 = " file://0003-Add-option-for-SEL-commands-for-Journal-based-SEL-en.patch"

DEPENDS:append:evb-npcm750= "evb-npcm750-yaml-config"

EXTRA_OECONF:evb-npcm750 = " \
    --with-journal-sel \
    SENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/evb-npcm750-yaml-config/ipmi-sensors.yaml \
    FRU_YAML_GEN=${STAGING_DIR_HOST}${datadir}/evb-npcm750-yaml-config/ipmi-fru-read.yaml \
    --disable-dynamic_sensors \
    "
