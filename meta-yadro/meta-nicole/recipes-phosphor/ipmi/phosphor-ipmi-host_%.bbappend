FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
DEPENDS_append = " nicole-yaml-config"

EXTRA_OECONF = " \
    SENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/nicole-yaml-config/ipmi-sensors.yaml \
    INVSENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/nicole-yaml-config/ipmi-inventory-sensors.yaml \
    FRU_YAML_GEN=${STAGING_DIR_HOST}${datadir}/nicole-yaml-config/ipmi-fru-read.yaml \
    "

SRC_URI_append  = "\
    file://0001-Add-support-for-persistent-only-settings.patch \
"
