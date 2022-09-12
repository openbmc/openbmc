FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

DEPENDS:append = " mtmitchell-yaml-config"

EXTRA_OEMESON = " \
                 -Dsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/mtmitchell-yaml-config/ipmi-sensors.yaml \
                 -Dfru-yaml-gen=${STAGING_DIR_HOST}${datadir}/mtmitchell-yaml-config/ipmi-fru-read.yaml \
                "
