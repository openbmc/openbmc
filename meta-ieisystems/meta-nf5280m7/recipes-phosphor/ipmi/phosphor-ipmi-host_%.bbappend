DEPENDS:append = " nf5280m7-yaml-config"

EXTRA_OEMESON= " \
    -Dsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/nf5280m7-yaml-config/ipmi-sensors.yaml \
    -Dinvsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/nf5280m7-yaml-config/ipmi-inventory-sensors.yaml \
    -Dfru-yaml-gen=${STAGING_DIR_HOST}${datadir}/nf5280m7-yaml-config/ipmi-fru-read.yaml \
    "
