DEPENDS:append:ibm-ac-server = " acx22-yaml-config"
DEPENDS:append:p10bmc = " p10bmc-yaml-config"

EXTRA_OEMESON:ibm-ac-server = " \
    -Dsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-sensors.yaml \
    -Dinvsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-inventory-sensors.yaml \
    -Dfru-yaml-gen=${STAGING_DIR_HOST}${datadir}/acx22-yaml-config/ipmi-fru-read.yaml \
    "

EXTRA_OEMESON:p10bmc = " \
    -Dsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/p10bmc-yaml-config/ipmi-sensors.yaml \
    -Dinvsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/p10bmc-yaml-config/ipmi-inventory-sensors.yaml \
    -Dfru-yaml-gen=${STAGING_DIR_HOST}${datadir}/p10bmc-yaml-config/ipmi-fru-read.yaml \
    "
