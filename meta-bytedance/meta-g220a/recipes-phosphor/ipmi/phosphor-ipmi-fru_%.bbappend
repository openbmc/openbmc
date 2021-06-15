DEPENDS_append_g220a= " g220a-yaml-config"

EXTRA_OECONF_g220a= " \
    YAML_GEN=${STAGING_DIR_HOST}${datadir}/g220a-yaml-config/ipmi-fru-read.yaml \
    PROP_YAML=${STAGING_DIR_HOST}${datadir}/g220a-yaml-config/ipmi-extra-properties.yaml \
    "
