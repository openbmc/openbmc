SUMMARY = "Phosphor OpenBMC IPMI daemon"
DESCRIPTION = "Phosphor OpenBMC IPMI router and plugin libraries"
HOMEPAGE = "http://github.com/openbmc/phosphor-host-ipmid"
PR = "r1"

RRECOMMENDS_${PN} += "packagegroup-obmc-ipmid-providers-libs"

inherit autotools pkgconfig
inherit obmc-phosphor-license
inherit obmc-phosphor-sdbus-service
inherit obmc-phosphor-ipmiprovider-symlink

DEPENDS += "phosphor-logging"
DEPENDS += "phosphor-mapper"
DEPENDS += "autoconf-archive-native"
DEPENDS += "packagegroup-obmc-ipmid-providers"
RDEPENDS_${PN}-dev += "phosphor-logging"
RDEPENDS_${PN}-dev += "phosphor-mapper-dev"
RDEPENDS_${PN} += "clear-once"
RDEPENDS_${PN} += "network"
RDEPENDS_${PN} += "libmapper"
RDEPENDS_${PN} += "phosphor-time-manager"
RRECOMMENDS_${PN} += "virtual-obmc-settings-mgmt"
SRC_URI += "git://github.com/openbmc/phosphor-host-ipmid"

SRCREV = "a66239bb3e1d8ba252f7cbf2691b7416f9bce32d"

# Setup IPMI Whitelist Conf files
WHITELIST_CONF = " \
        ${STAGING_DATADIR_NATIVE}/phosphor-ipmi-host/*.conf \
        ${S}/host-ipmid-whitelist.conf \
        "
EXTRA_OECONF = " \
        WHITELIST_CONF="${WHITELIST_CONF}" \
        "

S = "${WORKDIR}/git"

HOSTIPMI_PROVIDER_LIBRARY += "libapphandler.so"
HOSTIPMI_PROVIDER_LIBRARY += "libhostservice.so"
HOSTIPMI_PROVIDER_LIBRARY += "libsysintfcmds.so"

NETIPMI_PROVIDER_LIBRARY += "libapphandler.so"

FILES_${PN}_append = " ${libdir}/ipmid-providers/lib*${SOLIBS}"
FILES_${PN}_append = " ${libdir}/host-ipmid/lib*${SOLIBS}"
FILES_${PN}_append = " ${libdir}/net-ipmid/lib*${SOLIBS}"
FILES_${PN}-dev_append = " ${libdir}/ipmid-providers/lib*${SOLIBSDEV} ${libdir}/ipmid-providers/*.la"

DBUS_SERVICE_${PN} += "org.openbmc.HostServices.service"

# Soft Power Off
RDEPENDS_${PN} += "phosphor-mapper"

TMPL = "op-stop-host@.service"
INSTFMT = "op-stop-host@{0}.service"
TGTFMT = "obmc-stop-host@{0}.target"
FMT = "../${TMPL}:${TGTFMT}.wants/${INSTFMT}"

SYSTEMD_SERVICE_${PN} += "${TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'OBMC_HOST_INSTANCES')}"
