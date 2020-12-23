DEPENDS_append_ethanolx = " ethanolx-yaml-config"

EXTRA_OECONF_ethanolx = " \
    SENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/ethanolx-yaml-config/ipmi-sensors.yaml \
    FRU_YAML_GEN=${STAGING_DIR_HOST}${datadir}/ethanolx-yaml-config/ipmi-fru-read.yaml \
    "
