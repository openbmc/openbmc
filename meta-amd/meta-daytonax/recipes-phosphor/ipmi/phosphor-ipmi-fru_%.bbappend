DEPENDS:append:daytonax = " daytonax-yaml-config"

EXTRA_OECONF:daytonax = " \
    YAML_GEN=${STAGING_DIR_HOST}${datadir}/daytonax-yaml-config/ipmi-fru-read.yaml \
    PROP_YAML=${STAGING_DIR_HOST}${datadir}/daytonax-yaml-config/ipmi-extra-properties.yaml \
    "
