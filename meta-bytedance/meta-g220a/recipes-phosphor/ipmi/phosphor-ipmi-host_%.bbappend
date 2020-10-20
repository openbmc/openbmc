FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://0001-Disable-isFruPresent.patch"

DEPENDS_append_g220a= " g220a-yaml-config"

EXTRA_OECONF_g220a= " \
    SENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/g220a-yaml-config/ipmi-sensors.yaml \
    INVSENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/g220a-yaml-config/ipmi-inventory-sensors.yaml \
    FRU_YAML_GEN=${STAGING_DIR_HOST}${datadir}/g220a-yaml-config/ipmi-fru-read.yaml \
    "
