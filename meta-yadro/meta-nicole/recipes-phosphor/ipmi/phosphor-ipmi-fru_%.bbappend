DEPENDS_append = " nicole-yaml-config"

EXTRA_OECONF = " \
    YAML_GEN=${STAGING_DIR_HOST}${datadir}/nicole-yaml-config/ipmi-fru-read.yaml \
    "
