DEPENDS_append_fp5280g2= " fp5280g2-yaml-config"

EXTRA_OECONF_fp5280g2= " \
    SENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/fp5280g2-yaml-config/ipmi-sensors.yaml \
    INVSENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/fp5280g2-yaml-config/ipmi-inventory-sensors.yaml \
    FRU_YAML_GEN=${STAGING_DIR_HOST}${datadir}/fp5280g2-yaml-config/ipmi-fru-read.yaml \
    "
