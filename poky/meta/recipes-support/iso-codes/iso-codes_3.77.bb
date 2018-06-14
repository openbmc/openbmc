SUMMARY = "ISO language, territory, currency, script codes and their translations"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "git://salsa.debian.org/iso-codes-team/iso-codes.git;protocol=http"
SRCREV = "0a932d3e1e6d9058a6ef874c8ff1dc4a193bc030"

# inherit gettext cannot be used, because it adds gettext-native to BASEDEPENDS which
# are inhibited by allarch
DEPENDS = "gettext-native"

S = "${WORKDIR}/git"

inherit allarch autotools

FILES_${PN} += "${datadir}/xml/"
