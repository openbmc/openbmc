DEPENDS:append:fp5280g2= " fp5280g2-yaml-config"

EXTRA_OEMESON:fp5280g2= " \
    -Dsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/fp5280g2-yaml-config/ipmi-sensors.yaml \
    -Dinvsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/fp5280g2-yaml-config/ipmi-inventory-sensors.yaml \
    -Dfru-yaml-gen=${STAGING_DIR_HOST}${datadir}/fp5280g2-yaml-config/ipmi-fru-read.yaml \
    "
