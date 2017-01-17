SUMMARY = "Phosphor IPMI Inventory Plugin"
DESCRIPTION = "A Phosphor IPMI plugin that updates inventory."
PR = "r1"

inherit autotools pkgconfig
inherit obmc-phosphor-license
inherit obmc-phosphor-systemd

require ${PN}.inc

DEPENDS += " \
        systemd \
        phosphor-ipmi-host \
        phosphor-mapper \
        autoconf-archive-native \
        "

RDEPENDS_${PN} += "libsystemd"

SYSTEMD_SERVICE_${PN} += "obmc-read-eeprom@.service"

S = "${WORKDIR}/git"

FILES_${PN}_append = " ${libdir}/host-ipmid/lib*${SOLIBS}"
FILES_${PN}-dev_append = " ${libdir}/host-ipmid/lib*${SOLIBSDEV} ${libdir}/host-ipmid/*.la"
