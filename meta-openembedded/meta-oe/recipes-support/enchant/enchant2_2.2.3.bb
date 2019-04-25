SUMMARY = "Enchant Spell checker API Library v2"
SECTION = "libs"
HOMEPAGE = "https://abiword.github.io/enchant/"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=a916467b91076e631dd8edb7424769c7"

DEPENDS = "glib-2.0"

inherit autotools pkgconfig

SRC_URI = "https://github.com/AbiWord/enchant/releases/download/v${PV}/enchant-${PV}.tar.gz"
SRC_URI[md5sum] = "4b8abb58e00e93363591b3f5ea4f52d5"
SRC_URI[sha256sum] = "abd8e915675cff54c0d4da5029d95c528362266557c61c7149d53fa069b8076d"

S = "${WORKDIR}/enchant-${PV}"

EXTRA_OEMAKE = "pkgdatadir=${datadir}/enchant-2"

PACKAGECONFIG ??= "hunspell"
PACKAGECONFIG[aspell]  = "--with-aspell,--without-aspell,aspell,aspell"
PACKAGECONFIG[hunspell]  = "--with-hunspell,--without-hunspell,hunspell,hunspell"

FILES_${PN} += " \
    ${datadir}/enchant-2 \
    ${libdir}/enchant-2 \
"
