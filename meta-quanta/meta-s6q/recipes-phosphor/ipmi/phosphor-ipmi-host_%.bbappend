FILESEXTRAPATHS:prepend:s6q := "${THISDIR}/${PN}:"

DEPENDS:append:s6q = " s6q-yaml-config"

EXTRA_OECONF:append:s6q = " \
        FRU_YAML_GEN=${STAGING_DIR_HOST}${datadir}/s6q-yaml-config/ipmi-fru-read.yaml \
        SENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/s6q-yaml-config/ipmi-sensors.yaml \
        INVSENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/s6q-yaml-config/ipmi-inventory-sensors.yaml \
        "
