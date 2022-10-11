DEPENDS:append:ibm-ac-server = " acx22-yaml-config"
DEPENDS:append:p10bmc = " p10bmc-yaml-config"

EXTRA_OECONF:ibm-ac-server = " \
    YAML_GEN=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-fru-read.yaml \
    PROP_YAML=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-extra-properties.yaml \
    "

EXTRA_OECONF:p10bmc = " \
    YAML_GEN=${STAGING_DIR_HOST}${datadir}/p10bmc-yaml-config/ipmi-fru-read.yaml \
    "
