SUMMARY = "Enchant Spell checker API Library"
DESCRIPTION = "A library (and command-line program) that wraps a number of \
different spelling libraries and programs with a consistent interface."
SECTION = "libs"
HOMEPAGE = "https://abiword.github.io/enchant/"
BUGTRACKER = "https://github.com/AbiWord/enchant/issues/"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "glib-2.0"

inherit autotools pkgconfig

SRC_URI = "https://github.com/AbiWord/enchant/releases/download/v${PV}/enchant-${PV}.tar.gz"
SRC_URI[sha256sum] = "df68063b6c13b245fa7246b0e098a03e74f7a91c6d8947bc5c4f42ce55e2e41d"

UPSTREAM_CHECK_URI = "https://github.com/AbiWord/enchant/releases"

S = "${WORKDIR}/enchant-${PV}"

EXTRA_OEMAKE = "pkgdatadir=${datadir}/enchant-2"

PACKAGECONFIG ??= "aspell"
PACKAGECONFIG[aspell]  = "--with-aspell,--without-aspell,aspell,aspell"
PACKAGECONFIG[hunspell]  = "--with-hunspell,--without-hunspell,hunspell,hunspell"

FILES:${PN} += " \
    ${datadir}/enchant-2 \
    ${libdir}/enchant-2 \
"
FILES:${PN}-staticdev += "${libdir}/enchant-2/*.a"
