LICENSE = "GPLv3"
HOMEPAGE = "http://www.gnu.org/software/autoconf-archive/"
SECTION = "devel"

DEPENDS += "m4-native"
DEPENDS_class-native = "m4-native gnu-config-native"
DEPENDS_class-nativesdk = "m4-nativesdk gnu-config-nativesdk"

RDEPENDS_${PN} = "m4 gnu-config"
RDEPENDS_${PN}_class-native = "m4-native gnu-config-native"
RDEPENDS_${PN}_class-nativesdk = "m4-nativesdk gnu-config-nativesdk"

SRC_URI = "${GNU_MIRROR}/${BPN}/${BPN}-${PV}.tar.bz2"

inherit autotools
