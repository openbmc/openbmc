DEPENDS:append:ibm-ac-server = " acx22-yaml-config"
DEPENDS:append:mihawk = " acx22-yaml-config"
DEPENDS:append:p10bmc = " p10bmc-yaml-config"

# host watchdog does not work with witherspoon-tacoma host firmware
RDEPENDS:${PN}:remove:witherspoon-tacoma = "virtual/obmc-watchdog"

EXTRA_OECONF:ibm-ac-server = " \
    SENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-sensors.yaml \
    INVSENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-inventory-sensors.yaml \
    FRU_YAML_GEN=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-fru-read.yaml \
    "
EXTRA_OECONF:mihawk = " \
    SENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-sensors.yaml \
    INVSENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-inventory-sensors.yaml \
    FRU_YAML_GEN=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-fru-read.yaml \
    "
EXTRA_OECONF:p10bmc = " \
    FRU_YAML_GEN=${STAGING_DIR_HOST}${datadir}/p10bmc-yaml-config/ipmi-fru-read.yaml \
    "
