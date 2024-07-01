DEPENDS:append: = " nf5280m7-yaml-config"

EXTRA_OECONF:append = " \
        YAML_GEN=${STAGING_DIR_HOST}${datadir}/nf5280m7-yaml-config/ipmi-fru-read.yaml \
        PROP_YAML=${STAGING_DIR_HOST}${datadir}/nf5280m7-yaml-config/ipmi-extra-properties.yaml \
        "
