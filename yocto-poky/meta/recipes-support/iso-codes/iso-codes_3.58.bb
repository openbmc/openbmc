SUMMARY = "ISO language, territory, currency, script codes and their translations"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fbc093901857fcd118f065f900982c24"

SRC_URI = "https://pkg-isocodes.alioth.debian.org/downloads/iso-codes-${PV}.tar.xz"
SRC_URI[md5sum] = "34097a0085f0979e28f9db66ec274c5e"
SRC_URI[sha256sum] = "86af5735dce6e4eff2b983e5d8aa9a3dea1b8db702333ff20be89e45f7f35a72"

# inherit gettext cannot be used, because it adds gettext-native to BASEDEPENDS which
# are inhibited by allarch
DEPENDS = "gettext-native"

inherit allarch autotools

FILES_${PN} += "${datadir}/xml/"
