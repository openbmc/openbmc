SUMMARY = "Phosphor OpenBMC IPMI daemon"
DESCRIPTION = "Phosphor OpenBMC IPMI router and plugin libraries"
PR = "r1"

RRECOMMENDS_${PN} += "packagegroup-obmc-ipmid-providers-libs"

inherit autotools pkgconfig
inherit obmc-phosphor-license
inherit obmc-phosphor-sdbus-service
inherit obmc-phosphor-ipmiprovider-symlink
inherit phosphor-ipmi-host
inherit pythonnative
inherit obmc-phosphor-systemd

DEPENDS += "phosphor-logging"
DEPENDS += "phosphor-mapper"
DEPENDS += "autoconf-archive-native"
DEPENDS += "packagegroup-obmc-ipmid-providers"
DEPENDS += "virtual/phosphor-ipmi-sensor-inventory"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "obmc-targets"
DEPENDS += "virtual/phosphor-ipmi-inventory-sel"
DEPENDS += "virtual/phosphor-ipmi-fru-read-inventory"

RDEPENDS_${PN}-dev += "phosphor-logging"
RDEPENDS_${PN}-dev += "phosphor-mapper-dev"
RDEPENDS_${PN} += "clear-once"
RDEPENDS_${PN} += "network"
RDEPENDS_${PN} += "libmapper"
RDEPENDS_${PN} += "phosphor-time-manager"
RDEPENDS_${PN} += "sdbusplus phosphor-dbus-interfaces"
RDEPENDS_${PN} += "virtual/obmc-watchdog"

SYSTEMD_SERVICE_${PN} += "xyz.openbmc_project.Ipmi.Internal.SoftPowerOff.service phosphor-ipmi-host.service"

RRECOMMENDS_${PN} += "virtual-obmc-settings-mgmt"

require ${PN}.inc

# Setup IPMI Whitelist Conf files
WHITELIST_CONF = " \
        ${STAGING_DATADIR_NATIVE}/phosphor-ipmi-host/*.conf \
        ${S}/host-ipmid-whitelist.conf \
        "
EXTRA_OECONF = " \
        WHITELIST_CONF="${WHITELIST_CONF}" \
        SENSOR_YAML_GEN=${STAGING_DIR_NATIVE}${sensor_datadir}/sensor.yaml \
        INVSENSOR_YAML_GEN=${STAGING_DIR_NATIVE}${sensor_datadir}/invsensor.yaml \
        FRU_YAML_GEN=${STAGING_DIR_NATIVE}${config_datadir}/config.yaml \
        "

S = "${WORKDIR}/git"

HOSTIPMI_PROVIDER_LIBRARY += "libapphandler.so"
HOSTIPMI_PROVIDER_LIBRARY += "libsysintfcmds.so"

NETIPMI_PROVIDER_LIBRARY += "libapphandler.so"

FILES_${PN}_append = " ${libdir}/ipmid-providers/lib*${SOLIBS}"
FILES_${PN}_append = " ${libdir}/host-ipmid/lib*${SOLIBS}"
FILES_${PN}_append = " ${libdir}/net-ipmid/lib*${SOLIBS}"
FILES_${PN}-dev_append = " ${libdir}/ipmid-providers/lib*${SOLIBSDEV} ${libdir}/ipmid-providers/*.la"

# Soft Power Off
RDEPENDS_${PN} += "phosphor-mapper"

# install the soft power off service in the host shutdown target
SOFT_SVC = "xyz.openbmc_project.Ipmi.Internal.SoftPowerOff.service"
SOFT_TGTFMT = "obmc-host-shutdown@{0}.target"
SOFT_FMT = "../${SOFT_SVC}:${SOFT_TGTFMT}.requires/${SOFT_SVC}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'SOFT_FMT', 'OBMC_HOST_INSTANCES')}"
