DEPENDS:append:romulus = " romulus-yaml-config"

EXTRA_OEMESON:romulus = " \
    -Dsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/romulus-yaml-config/ipmi-sensors.yaml \
    -Dinvsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/romulus-yaml-config/ipmi-inventory-sensors.yaml \
    -Dfru-yaml-gen=${STAGING_DIR_HOST}${datadir}/romulus-yaml-config/ipmi-fru-read.yaml \
    "
