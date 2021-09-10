DEPENDS:append:romulus = " romulus-yaml-config"

EXTRA_OECONF:romulus = " \
    INVSENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/romulus-yaml-config/ipmi-inventory-sensors.yaml \
    "
