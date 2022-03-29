FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

DEPENDS:append = " ${MACHINE}-yaml-config"

EXTRA_OECONF = " \
                SENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/${MACHINE}-yaml-config/ipmi-sensors.yaml \
               "

