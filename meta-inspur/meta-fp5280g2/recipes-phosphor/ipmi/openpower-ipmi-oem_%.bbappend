DEPENDS_append_fp5280g2= " fp5280g2-yaml-config"

EXTRA_OECONF_fp5280g2= " \
    INVSENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/fp5280g2-yaml-config/ipmi-inventory-sensors.yaml \
    "
