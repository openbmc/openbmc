FILESEXTRAPATHS_append_mtjade := "${THISDIR}/${PN}:"

DEPENDS_append_mtjade = " mtjade-yaml-config"

RRECOMMENDS_${PN} += "ipmitool"

EXTRA_OECONF_mtjade = " \
    SENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/mtjade-yaml-config/ipmi-sensors-${MACHINE}.yaml \
    "
