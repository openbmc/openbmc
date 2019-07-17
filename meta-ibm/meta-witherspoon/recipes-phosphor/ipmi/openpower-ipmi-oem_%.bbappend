DEPENDS_append_ibm-ac-server = " acx22-yaml-config"

EXTRA_OECONF_ibm-ac-server = " \
    INVSENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-inventory-sensors.yaml \
    "
