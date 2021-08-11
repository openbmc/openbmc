DEPENDS:append:zaius = " zaius-yaml-config"

EXTRA_OECONF:zaius = " \
    SENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/zaius-yaml-config/ipmi-sensors.yaml \
    FRU_YAML_GEN=${STAGING_DIR_HOST}${datadir}/zaius-yaml-config/ipmi-fru-read.yaml \
    "
