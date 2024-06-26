DEPENDS:append:gbs = " gbs-yaml-config"

FILESEXTRAPATHS:prepend:gbs := "${THISDIR}/${PN}:"
SRC_URI:append:gbs = " file://gbs-ipmid-whitelist.conf"

WHITELIST_CONF:gbs = "${UNPACKDIR}/gbs-ipmid-whitelist.conf"

EXTRA_OEMESON:append:gbs = " \
     -Dsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/gbs-yaml-config/ipmi-sensors.yaml \
     -Dfru-yaml-gen=${STAGING_DIR_HOST}${datadir}/gbs-yaml-config/ipmi-fru-read.yaml \
     -Dinvsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/gbs-yaml-config/ipmi-inventory-sensors.yaml \
     "

RDEPENDS:${PN}:remove:gbs = "clear-once"
