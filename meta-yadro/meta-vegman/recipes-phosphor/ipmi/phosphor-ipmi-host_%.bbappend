FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

PACKAGECONFIG:append = " dynamic-sensors hybrid-sensors"

DEPENDS:append= " vegman-yaml-config"
EXTRA_OEMESON= " \
    -Dsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/vegman-yaml-config/ipmi-sensors-static.yaml \
    "
