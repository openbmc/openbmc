DEPENDS:append:zaius = " zaius-yaml-config"

EXTRA_OEMESON:zaius = " \
    -Dsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/zaius-yaml-config/ipmi-sensors.yaml \
    -Dfru-yaml-gen=${STAGING_DIR_HOST}${datadir}/zaius-yaml-config/ipmi-fru-read.yaml \
    "
