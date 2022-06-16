FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

DEPENDS:append = " ${MACHINE}-yaml-config"

EXTRA_OEMESON = " \
                -Dsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/${MACHINE}-yaml-config/ipmi-sensors.yaml \
               "

