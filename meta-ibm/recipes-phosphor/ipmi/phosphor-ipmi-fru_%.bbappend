DEPENDS:append:ibm-ac-server = " acx22-yaml-config"
DEPENDS:append:mihawk = " acx22-yaml-config"

EXTRA_OECONF:ibm-ac-server = " \
    YAML_GEN=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-fru-read.yaml \
    PROP_YAML=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-extra-properties.yaml \
    "
EXTRA_OECONF:mihawk = " \
    YAML_GEN=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-fru-read.yaml \
    PROP_YAML=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-extra-properties.yaml \
    "
