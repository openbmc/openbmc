DEPENDS_append_ibm-ac-server = " acx22-yaml-config"
DEPENDS_append_mihawk = " acx22-yaml-config"

EXTRA_OECONF_ibm-ac-server = " \
    YAML_GEN=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-fru-read.yaml \
    PROP_YAML=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-extra-properties.yaml \
    "
EXTRA_OECONF_mihawk = " \
    YAML_GEN=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-fru-read.yaml \
    PROP_YAML=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-extra-properties.yaml \
    "
