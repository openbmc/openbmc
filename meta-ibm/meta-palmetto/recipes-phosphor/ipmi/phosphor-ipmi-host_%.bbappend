DEPENDS:append:palmetto = " palmetto-yaml-config"

EXTRA_OEMESON:palmetto = " \
    -Dsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/palmetto-yaml-config/ipmi-sensors.yaml \
    -Dfru-yaml-gen=${STAGING_DIR_HOST}${datadir}/palmetto-yaml-config/ipmi-fru-read.yaml \
    "
