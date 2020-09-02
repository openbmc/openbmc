DEPENDS_append_gbs = " gbs-yaml-config"

SRC_URI_remove_gbs = "git://github.com/openbmc/phosphor-host-ipmid"
SRC_URI_prepend_gbs = "git://github.com/quanta-bmc/phosphor-host-ipmid.git"

SRCREV_gbs = "257ea262809c52e51f1f592e047d27125f0273b3"

FILESEXTRAPATHS_prepend_gbs := "${THISDIR}/${PN}:"
SRC_URI_append_gbs = " file://gbs-ipmid-whitelist.conf \
                       file://0063-Save-the-pre-timeout-interrupt-in-dbus-property.patch \
                     "

WHITELIST_CONF_gbs = "${WORKDIR}/gbs-ipmid-whitelist.conf"

EXTRA_OECONF_append_gbs = " --with-journal-sel \
     SENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/gbs-yaml-config/ipmi-sensors.yaml \
     FRU_YAML_GEN=${STAGING_DIR_HOST}${datadir}/gbs-yaml-config/ipmi-fru-read.yaml \
     "
