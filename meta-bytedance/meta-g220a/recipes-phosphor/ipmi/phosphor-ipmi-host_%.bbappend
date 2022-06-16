FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

DEPENDS:append:g220a= " g220a-yaml-config"

EXTRA_OEMESON:g220a= " \
    -Dsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/g220a-yaml-config/ipmi-sensors.yaml \
    -Dinvsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/g220a-yaml-config/ipmi-inventory-sensors.yaml \
    -Dfru-yaml-gen=${STAGING_DIR_HOST}${datadir}/g220a-yaml-config/ipmi-fru-read.yaml \
    "
