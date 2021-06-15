DEPENDS_append = " nicole-yaml-config"

EXTRA_OECONF = " \
    INVSENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/nicole-yaml-config/ipmi-inventory-sensors.yaml \
    "
