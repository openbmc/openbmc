SUMMARY = "Enchant Spell checker API Library"
DESCRIPTION = "A library (and command-line program) that wraps a number of \
different spelling libraries and programs with a consistent interface."
SECTION = "libs"
HOMEPAGE = "https://abiword.github.io/enchant/"
BUGTRACKER = "https://github.com/AbiWord/enchant/issues/"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=a916467b91076e631dd8edb7424769c7"

DEPENDS = "glib-2.0"

inherit autotools pkgconfig

SRC_URI = "https://github.com/AbiWord/enchant/releases/download/v${PV}/enchant-${PV}.tar.gz"
SRC_URI[md5sum] = "c7b9d6a392ecb8758e499f783e8dc883"
SRC_URI[sha256sum] = "c7b5e2853f0dd0b1aafea2f9e071941affeec3a76df8e3f6d67a718c89293555"

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
