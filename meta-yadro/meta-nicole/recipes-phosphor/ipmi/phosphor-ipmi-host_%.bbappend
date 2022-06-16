DEPENDS:append = " nicole-yaml-config"

EXTRA_OEMESON = " \
    -Dsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/nicole-yaml-config/ipmi-sensors.yaml \
    -Dinvsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/nicole-yaml-config/ipmi-inventory-sensors.yaml \
    -Dfru-yaml-gen=${STAGING_DIR_HOST}${datadir}/nicole-yaml-config/ipmi-fru-read.yaml \
    "
