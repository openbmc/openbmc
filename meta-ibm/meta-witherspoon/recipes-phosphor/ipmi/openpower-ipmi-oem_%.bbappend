DEPENDS_append = " acx22-yaml-config"

EXTRA_OECONF = " \
    INVSENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-inventory-sensors.yaml \
    "
