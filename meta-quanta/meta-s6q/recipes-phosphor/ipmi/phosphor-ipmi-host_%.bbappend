FILESEXTRAPATHS:prepend:s6q := "${THISDIR}/${PN}:"

DEPENDS:append:s6q = " s6q-yaml-config"

EXTRA_OEMESON:append:s6q = " \
        -Dfru-yaml-gen=${STAGING_DIR_HOST}${datadir}/s6q-yaml-config/ipmi-fru-read.yaml \
        -Dsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/s6q-yaml-config/ipmi-sensors.yaml \
        -Dinvsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/s6q-yaml-config/ipmi-inventory-sensors.yaml \
        "
