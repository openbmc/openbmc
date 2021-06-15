DEPENDS_append_fp5280g2= " fp5280g2-yaml-config"

EXTRA_OECONF_fp5280g2= " \
    YAML_GEN=${STAGING_DIR_HOST}${datadir}/fp5280g2-yaml-config/ipmi-fru-read.yaml \
    PROP_YAML=${STAGING_DIR_HOST}${datadir}/fp5280g2-yaml-config/ipmi-extra-properties.yaml \
    "
