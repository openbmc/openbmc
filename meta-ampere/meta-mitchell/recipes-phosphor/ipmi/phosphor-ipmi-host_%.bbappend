FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

PACKAGECONFIG:append = " dynamic-storages-only"
DEPENDS:append = " mtmitchell-yaml-config"

EXTRA_OEMESON = " \
                 -Dsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/mtmitchell-yaml-config/ipmi-sensors.yaml \
                 -Dfru-yaml-gen=${STAGING_DIR_HOST}${datadir}/mtmitchell-yaml-config/ipmi-fru-read.yaml \
                "
