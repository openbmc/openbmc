DEPENDS_append_gbs = " gbs-yaml-config"

FILESEXTRAPATHS_prepend_gbs := "${THISDIR}/${PN}:"
SRC_URI_append_gbs = " file://gbs-ipmid-whitelist.conf"

WHITELIST_CONF_gbs = "${WORKDIR}/gbs-ipmid-whitelist.conf"

EXTRA_OECONF_append_gbs = " \
     SENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/gbs-yaml-config/ipmi-sensors.yaml \
     FRU_YAML_GEN=${STAGING_DIR_HOST}${datadir}/gbs-yaml-config/ipmi-fru-read.yaml \
     INVSENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/gbs-yaml-config/ipmi-inventory-sensors.yaml \
     "

RDEPENDS_${PN}_remove_gbs = "clear-once"
