DEPENDS_append_gbs = " gbs-yaml-config"

FILESEXTRAPATHS_prepend_gbs := "${THISDIR}/${PN}:"
SRC_URI_append_gbs = " file://gbs-ipmid-whitelist.conf \
                       file://0001-Add-Chassis-State-Transition-interface.patch \
                       file://0002-Update-Host-State-Transition-function.patch \
                       file://0003-Update-IPMI-Chassis-Control-command-transition-reque.patch \
                       file://0001-Fix-issues-and-support-signed-sensor-values.patch \
                     "

WHITELIST_CONF_gbs = "${WORKDIR}/gbs-ipmid-whitelist.conf"

EXTRA_OECONF_append_gbs = " \
     SENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/gbs-yaml-config/ipmi-sensors.yaml \
     FRU_YAML_GEN=${STAGING_DIR_HOST}${datadir}/gbs-yaml-config/ipmi-fru-read.yaml \
     INVSENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/gbs-yaml-config/ipmi-inventory-sensors.yaml \
     "

RDEPENDS_${PN}_remove_gbs = "clear-once"
