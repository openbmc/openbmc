DEPENDS_append_ibm-ac-server = " acx22-yaml-config"
DEPENDS_append_mihawk = " acx22-yaml-config"

EXTRA_OECONF_ibm-ac-server = " \
    INVSENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-inventory-sensors.yaml \
    "
EXTRA_OECONF_mihawk = " \
    INVSENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-inventory-sensors.yaml \
    "