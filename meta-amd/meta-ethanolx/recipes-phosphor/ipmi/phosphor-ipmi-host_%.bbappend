DEPENDS:append:ethanolx = " ethanolx-yaml-config"

EXTRA_OEMESON:ethanolx = " \
    -Dsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/ethanolx-yaml-config/ipmi-sensors.yaml \
    -Dfru-yaml-gen=${STAGING_DIR_HOST}${datadir}/ethanolx-yaml-config/ipmi-fru-read.yaml \
    "
