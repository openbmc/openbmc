DEPENDS:append:romulus = " romulus-yaml-config"

EXTRA_OECONF:romulus = " \
    SENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/romulus-yaml-config/ipmi-sensors.yaml \
    INVSENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/romulus-yaml-config/ipmi-inventory-sensors.yaml \
    FRU_YAML_GEN=${STAGING_DIR_HOST}${datadir}/romulus-yaml-config/ipmi-fru-read.yaml \
    "
