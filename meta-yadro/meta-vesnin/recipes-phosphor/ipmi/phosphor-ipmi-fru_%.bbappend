DEPENDS_append_vesnin = " vesnin-yaml-config"

EXTRA_OECONF_vesnin = " \
    YAML_GEN=${STAGING_DIR_HOST}${datadir}/vesnin-yaml-config/ipmi-fru-read.yaml \
    "
