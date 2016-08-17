SUMMARY = "ISO language, territory, currency, script codes and their translations"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fbc093901857fcd118f065f900982c24"

SRC_URI = "https://pkg-isocodes.alioth.debian.org/downloads/iso-codes-${PV}.tar.xz"
SRC_URI[md5sum] = "890a08d4f962748e0a0758a8aa471896"
SRC_URI[sha256sum] = "834de5193c8489eedeaf6509457a9b13476702386ae1f3ed4f391a349d630320"

# inherit gettext cannot be used, because it adds gettext-native to BASEDEPENDS which
# are inhibited by allarch
DEPENDS = "gettext-native"

inherit allarch autotools

FILES_${PN} += "${datadir}/xml/"
