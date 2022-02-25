SUMMARY = "GNU Aspell spell-checker"

DESCRIPTION = "Spell checker designed to eventually replace Ispell. \
It can either be used as a library or as an independent spell checker. \
Its main feature is that it does a superior job of suggesting possible \
replacements for a misspelled word than just about any other spell \
checker out there for the English language."

SECTION = "console/utils"

HOMEPAGE = "http://aspell.net/"

LICENSE = "LGPL-2.0-only | LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"

SRC_URI = "${GNU_MIRROR}/aspell/aspell-${PV}.tar.gz \
           file://CVE-2019-25051.patch \
"
SRC_URI[md5sum] = "012fa9209203ae4e5a61c2a668fd10e3"
SRC_URI[sha256sum] = "f9b77e515334a751b2e60daab5db23499e26c9209f5e7b7443b05235ad0226f2"

PACKAGECONFIG ??= ""
PACKAGECONFIG[curses] = "--enable-curses,--disable-curses,ncurses"

PACKAGES += "libaspell libpspell aspell-utils"

RDEPENDS:${PN}-utils += "perl"

FILES:libaspell = "${libdir}/libaspell.so.* ${libdir}/aspell*"
FILES:aspell-utils = "${bindir}/word-list-compress ${bindir}/aspell-import ${bindir}/run-with-aspell ${bindir}/pre*"
FILES:${PN} = "${bindir}/aspell"
FILES:libpspell = "${libdir}/libpspell.so.*"
FILES:${PN}-dev += "${bindir}/pspell-config"

ARM_INSTRUCTION_SET:armv4 = "arm"
ARM_INSTRUCTION_SET:armv5 = "arm"
ARM_INSTRUCTION_SET:armv6 = "arm"

inherit autotools-brokensep gettext texinfo binconfig-disabled

BINCONFIG = "${bindir}/pspell-config"
