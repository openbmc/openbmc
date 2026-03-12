SUMMARY = "ISO language, territory, currency, script codes and their translations"
DESCRIPTION = "Provides lists of various ISO standards (e.g. country, \
language, language scripts, and currency names) in one place, rather \
than repeated in many programs throughout the system."
HOMEPAGE = "https://salsa.debian.org/iso-codes-team/iso-codes"
BUGTRACKER = "https://salsa.debian.org/iso-codes-team/iso-codes/issues"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LICENSES/LGPL-2.1-or-later.txt;md5=310c7c93cf5181c6b0cc8229a1f3c6f6 \
                    file://REUSE.toml;md5=222088771af8590daba65c39e4747321"

SRC_URI = "git://salsa.debian.org/iso-codes-team/iso-codes.git;protocol=https;branch=main;tag=v${PV}"
SRCREV = "74623235ae99f6e835e1e465017ce4c544ae6b53"

# inherit gettext cannot be used, because it adds gettext-native to BASEDEPENDS which
# are inhibited by allarch
DEPENDS = "gettext-native"

inherit allarch meson

FILES:${PN} += "${datadir}/xml/"

BBCLASSEXTEND += "native"
