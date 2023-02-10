FILESEXTRAPATHS:prepend := "${THISDIR}/phosphor-ipmi-host:"

DEPENDS:append:daytonax = " daytonax-yaml-config"

PACKAGECONFIG:remove = " i2c-allowlist"

EXTRA_OEMESON:daytonax = " \
    -Dsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/daytonax-yaml-config/ipmi-sensors.yaml \
    -Dfru-yaml-gen=${STAGING_DIR_HOST}${datadir}/daytonax-yaml-config/ipmi-fru-read.yaml \
    "
