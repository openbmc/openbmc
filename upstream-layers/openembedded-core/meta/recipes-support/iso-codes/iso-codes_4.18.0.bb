SUMMARY = "ISO language, territory, currency, script codes and their translations"
DESCRIPTION = "Provides lists of various ISO standards (e.g. country, \
language, language scripts, and currency names) in one place, rather \
than repeated in many programs throughout the system."
HOMEPAGE = "https://salsa.debian.org/iso-codes-team/iso-codes"
BUGTRACKER = "https://salsa.debian.org/iso-codes-team/iso-codes/issues"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4bf661c1e3793e55c8d1051bc5e0ae21"

SRC_URI = "git://salsa.debian.org/iso-codes-team/iso-codes.git;protocol=https;branch=main;tag=v${PV}"
SRCREV = "5be4d112d420706eacd913a3ebd85549fe9eeae4"

# inherit gettext cannot be used, because it adds gettext-native to BASEDEPENDS which
# are inhibited by allarch
DEPENDS = "gettext-native"

inherit allarch autotools

FILES:${PN} += "${datadir}/xml/"

BBCLASSEXTEND += "native"
