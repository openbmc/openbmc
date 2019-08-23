DEPENDS_append = " nicole-yaml-config"

EXTRA_OECONF = " \
    SENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/nicole-yaml-config/ipmi-sensors.yaml \
    INVSENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/nicole-yaml-config/ipmi-inventory-sensors.yaml \
    FRU_YAML_GEN=${STAGING_DIR_HOST}${datadir}/nicole-yaml-config/ipmi-fru-read.yaml \
    "
