DEPENDS:append:gbs = " gbs-yaml-config"

FILESEXTRAPATHS:prepend:gbs := "${THISDIR}/${PN}:"
SRC_URI:append:gbs = " file://gbs-ipmid-whitelist.conf"

WHITELIST_CONF:gbs = "${WORKDIR}/gbs-ipmid-whitelist.conf"

EXTRA_OECONF:append:gbs = " \
     SENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/gbs-yaml-config/ipmi-sensors.yaml \
     FRU_YAML_GEN=${STAGING_DIR_HOST}${datadir}/gbs-yaml-config/ipmi-fru-read.yaml \
     INVSENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/gbs-yaml-config/ipmi-inventory-sensors.yaml \
     "

RDEPENDS:${PN}:remove:gbs = "clear-once"
