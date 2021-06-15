DEPENDS_append_ethanolx = " ethanolx-yaml-config"

EXTRA_OECONF_ethanolx = " \
    YAML_GEN=${STAGING_DIR_HOST}${datadir}/ethanolx-yaml-config/ipmi-fru-read.yaml \
    PROP_YAML=${STAGING_DIR_HOST}${datadir}/ethanolx-yaml-config/ipmi-extra-properties.yaml \
    "
