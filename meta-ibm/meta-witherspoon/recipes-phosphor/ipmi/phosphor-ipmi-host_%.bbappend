DEPENDS_append_ibm-ac-server = " acx22-yaml-config"

EXTRA_OECONF_ibm-ac-server = " \
    SENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-sensors.yaml \
    INVSENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-inventory-sensors.yaml \
    FRU_YAML_GEN=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-fru-read.yaml \
    "
