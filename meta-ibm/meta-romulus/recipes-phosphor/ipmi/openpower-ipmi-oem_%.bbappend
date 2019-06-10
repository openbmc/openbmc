DEPENDS_append_romulus = " romulus-yaml-config"

EXTRA_OECONF_romulus = " \
    INVSENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/romulus-yaml-config/ipmi-inventory-sensors.yaml \
    "
