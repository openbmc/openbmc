SUMMARY = "ISO language, territory, currency, script codes and their translations"
DESCRIPTION = "Provides lists of various ISO standards (e.g. country, \
language, language scripts, and currency names) in one place, rather \
than repeated in many programs throughout the system."
HOMEPAGE = "https://salsa.debian.org/iso-codes-team/iso-codes"
BUGTRACKER = "https://salsa.debian.org/iso-codes-team/iso-codes/issues"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "git://salsa.debian.org/iso-codes-team/iso-codes.git;protocol=http;branch=main;"
SRCREV = "a36019e5014bff251f83d522ddcfebaecf52afd3"

# inherit gettext cannot be used, because it adds gettext-native to BASEDEPENDS which
# are inhibited by allarch
DEPENDS = "gettext-native"

S = "${WORKDIR}/git"

inherit allarch autotools

FILES_${PN} += "${datadir}/xml/"
