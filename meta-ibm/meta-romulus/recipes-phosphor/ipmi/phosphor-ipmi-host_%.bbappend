DEPENDS:append:romulus = " romulus-yaml-config"

# entity-manager is not installed on Romulus (uses MRW inventory), so the
# decorator code is dead weight. Scoped to romulus only: other platforms
# (e.g. asrock) use entity-manager and keep this enabled.
PACKAGECONFIG:remove:romulus = "entity-manager-decorators"

EXTRA_OEMESON:romulus = " \
    -Dsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/romulus-yaml-config/ipmi-sensors.yaml \
    -Dinvsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/romulus-yaml-config/ipmi-inventory-sensors.yaml \
    -Dfru-yaml-gen=${STAGING_DIR_HOST}${datadir}/romulus-yaml-config/ipmi-fru-read.yaml \
    "
