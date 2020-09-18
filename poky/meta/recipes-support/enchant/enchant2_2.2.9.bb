SUMMARY = "Enchant Spell checker API Library"
SECTION = "libs"
HOMEPAGE = "https://abiword.github.io/enchant/"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=a916467b91076e631dd8edb7424769c7"

DEPENDS = "glib-2.0"

inherit autotools pkgconfig

SRC_URI = "https://github.com/AbiWord/enchant/releases/download/v${PV}/enchant-${PV}.tar.gz"
SRC_URI[sha256sum] = "b29a3d2273f5edcbdbbb565e94bfd8ea3f9526886fcb6327b4b0f72f0d722f3c"

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
