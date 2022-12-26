FILESEXTRAPATHS:append:evb-npcm750 := "${THISDIR}/${PN}:"

DEPENDS:append:evb-npcm750 = " evb-npcm750-yaml-config"
EXTRA_OECONF:append:evb-npcm750 = " SENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/evb-npcm750-yaml-config/ipmi-sensors.yaml"
EXTRA_OECONF:append:evb-npcm750 = " FRU_YAML_GEN=${STAGING_DIR_HOST}${datadir}/evb-npcm750-yaml-config/ipmi-fru-read.yaml"
EXTRA_OECONF:append:evb-npcm750 = " INVSENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/evb-npcm750-yaml-config/ipmi-inventory-sensors.yaml"
EXTRA_OECONF:append:evb-npcm750 = " --disable-i2c-whitelist-check"
EXTRA_OECONF:append:evb-npcm750 = " --enable-sel_logger_clears_sel"

# avoid build error after remove ipmi-fru
WHITELIST_CONF:evb-npcm750 = "${S}/host-ipmid-whitelist.conf"
