DEPENDS:append:fp5280g2= " fp5280g2-yaml-config"

EXTRA_OECONF:fp5280g2= ""

IPMI_FRU_YAML:fp5280g2="${STAGING_DIR_HOST}${datadir}/fp5280g2-yaml-config/ipmi-fru-read.yaml"
IPMI_FRU_PROP_YAML:fp5280g2="${STAGING_DIR_HOST}${datadir}/fp5280g2-yaml-config/ipmi-extra-properties.yaml"
