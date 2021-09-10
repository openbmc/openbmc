DEPENDS:append:palmetto = " palmetto-yaml-config"

EXTRA_OECONF:palmetto = " \
    SENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/palmetto-yaml-config/ipmi-sensors.yaml \
    FRU_YAML_GEN=${STAGING_DIR_HOST}${datadir}/palmetto-yaml-config/ipmi-fru-read.yaml \
    "
