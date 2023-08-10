FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

PACKAGECONFIG:append = " dynamic-storages-only"
DEPENDS:append = " mtjade-yaml-config"

EXTRA_OEMESON = " \
                -Dsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/mtjade-yaml-config/ipmi-sensors.yaml \
                -Dfru-yaml-gen=${STAGING_DIR_HOST}${datadir}/mtjade-yaml-config/ipmi-fru-read.yaml \
               "

