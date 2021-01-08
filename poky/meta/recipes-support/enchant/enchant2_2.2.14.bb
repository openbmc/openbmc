SUMMARY = "Enchant Spell checker API Library"
SECTION = "libs"
HOMEPAGE = "https://abiword.github.io/enchant/"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "glib-2.0"

inherit autotools pkgconfig

SRC_URI = "https://github.com/AbiWord/enchant/releases/download/v${PV}/enchant-${PV}.tar.gz"
SRC_URI[sha256sum] = "bf7cc9a410b54c29648c7711dc8f210e7b13768508aaf43cc9c4527d5a9c7a8e"

UPSTREAM_CHECK_URI = "https://github.com/AbiWord/enchant/releases"

S = "${WORKDIR}/enchant-${PV}"

EXTRA_OEMAKE = "pkgdatadir=${datadir}/enchant-2"

PACKAGECONFIG ??= "aspell"
PACKAGECONFIG[aspell]  = "--with-aspell,--without-aspell,aspell,aspell"
PACKAGECONFIG[hunspell]  = "--with-hunspell,--without-hunspell,hunspell,hunspell"

FILES_${PN} += " \
    ${datadir}/enchant-2 \
    ${libdir}/enchant-2 \
"
FILES_${PN}-staticdev += "${libdir}/enchant-2/*.a"
