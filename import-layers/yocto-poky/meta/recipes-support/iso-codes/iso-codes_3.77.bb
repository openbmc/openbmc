SUMMARY = "ISO language, territory, currency, script codes and their translations"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "https://pkg-isocodes.alioth.debian.org/downloads/iso-codes-${PV}.tar.xz"
SRC_URI[md5sum] = "9d0d06cfb4634428b300845edcd7140a"
SRC_URI[sha256sum] = "21cd73a4c6f95d9474ebfcffd4e065223857720f24858e564f4409b19f7f0d90"

# inherit gettext cannot be used, because it adds gettext-native to BASEDEPENDS which
# are inhibited by allarch
DEPENDS = "gettext-native"

inherit allarch autotools

FILES_${PN} += "${datadir}/xml/"
