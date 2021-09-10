DEPENDS:append:palmetto = " palmetto-yaml-config"

EXTRA_OECONF:palmetto = " \
    YAML_GEN=${STAGING_DIR_HOST}${datadir}/palmetto-yaml-config/ipmi-fru-read.yaml \
    PROP_YAML=${STAGING_DIR_HOST}${datadir}/palmetto-yaml-config/ipmi-extra-properties.yaml \
    "
