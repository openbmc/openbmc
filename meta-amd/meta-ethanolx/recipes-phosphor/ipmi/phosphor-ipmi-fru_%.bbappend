DEPENDS:append:ethanolx = " ethanolx-yaml-config"

EXTRA_OECONF:ethanolx = " \
    YAML_GEN=${STAGING_DIR_HOST}${datadir}/ethanolx-yaml-config/ipmi-fru-read.yaml \
    PROP_YAML=${STAGING_DIR_HOST}${datadir}/ethanolx-yaml-config/ipmi-extra-properties.yaml \
    "
