SUMMARY = "Enchant Spell checker API Library"
DESCRIPTION = "A library (and command-line program) that wraps a number of \
different spelling libraries and programs with a consistent interface."
SECTION = "libs"
HOMEPAGE = "https://rrthomas.github.io/enchant/"
BUGTRACKER = "https://github.com/rrthomas/enchant/issues"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "glib-2.0 groff-native"

inherit autotools pkgconfig github-releases

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/enchant-${PV}.tar.gz"
SRC_URI[sha256sum] = "8e7f6cb0c3b79be3146eb3ab93650484adbc59dae5f2c1958fde557080ba678c"

GITHUB_BASE_URI = "https://github.com/rrthomas/enchant/releases"

S = "${UNPACKDIR}/enchant-${PV}"

PACKAGECONFIG ??= "aspell"
PACKAGECONFIG[aspell]  = "--with-aspell,--without-aspell,aspell,aspell"
PACKAGECONFIG[hunspell]  = "--with-hunspell,--without-hunspell,hunspell,hunspell"

FILES:${PN} += " \
    ${datadir}/enchant-2 \
    ${libdir}/enchant-2 \
"
FILES:${PN}-staticdev += "${libdir}/enchant-2/*.a"
