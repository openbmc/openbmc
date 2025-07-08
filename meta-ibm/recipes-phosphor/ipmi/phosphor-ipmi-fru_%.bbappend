DEPENDS:append:ibm-ac-server = " acx22-yaml-config"
DEPENDS:append:p10bmc = " p10bmc-yaml-config"

IPMI_FRU_YAML:ibm-ac-server = "${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-fru-read.yaml"
IPMI_FRU_PROP_YAML:ibm-ac-server = "${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-extra-properties.yaml"

IPMI_FRU_YAML:p10bmc = "${STAGING_DIR_HOST}${datadir}/p10bmc-yaml-config/ipmi-fru-read.yaml"
