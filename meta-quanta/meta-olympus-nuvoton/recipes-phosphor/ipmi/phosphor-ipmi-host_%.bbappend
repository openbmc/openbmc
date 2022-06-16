DEPENDS:append:olympus-nuvoton = " olympus-nuvoton-yaml-config"

EXTRA_OEMESON:olympus-nuvoton = " \
    -Dsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/olympus-nuvoton-yaml-config/ipmi-sensors.yaml \
    -Dfru-yaml-gen=${STAGING_DIR_HOST}${datadir}/olympus-nuvoton-yaml-config/ipmi-fru-read.yaml \
    "
