DEPENDS:append:ibm-ac-server = " acx22-yaml-config"
DEPENDS:append:mihawk = " acx22-yaml-config"

EXTRA_OECONF:ibm-ac-server = " \
    INVSENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-inventory-sensors.yaml \
    "
EXTRA_OECONF:mihawk = " \
    INVSENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-inventory-sensors.yaml \
    "