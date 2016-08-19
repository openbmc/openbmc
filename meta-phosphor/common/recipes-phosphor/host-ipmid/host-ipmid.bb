SUMMARY = "Phosphor OpenBMC IPMI daemon"
DESCRIPTION = "Phosphor OpenBMC IPMI router and plugin libraries"
HOMEPAGE = "http://github.com/openbmc/phosphor-host-ipmid"
PR = "r1"

RRECOMMENDS_${PN} += "virtual/obmc-phosphor-host-ipmi-hw"
RRECOMMENDS_${PN} += "packagegroup-obmc-ipmid-providers"

inherit obmc-phosphor-license
inherit obmc-phosphor-sdbus-service
inherit obmc-phosphor-c-daemon

TARGET_CFLAGS   += "-fpic"

DEPENDS += "obmc-mapper"
DEPENDS += "packagegroup-obmc-ipmid-whitelists-native"
RDEPENDS_${PN} += "clear-once"
RDEPENDS_${PN} += "settings"
RDEPENDS_${PN} += "network"
RDEPENDS_${PN} += "libmapper"
SRC_URI += "git://github.com/openbmc/phosphor-host-ipmid"

SRCREV = "bc40c178bb0b345ed1edf553b94369330003af34"
WHITELIST_CONF = "${STAGING_ETCDIR_NATIVE}/host-ipmid-conf/*.conf"
export WHITELIST_CONF += "${S}/host-ipmid-whitelist.conf"

S = "${WORKDIR}/git"
INSTALL_NAME = "ipmid"

do_install() {
        install -m 0755 -d ${D}${libdir}/host-ipmid
        install -m 0755 ${S}/*.so ${D}${libdir}/host-ipmid/

        install -m 0755 -d ${D}${includedir}/host-ipmid
        install -m 0644 ${S}/ipmid-api.h ${D}${includedir}/host-ipmid/
}
