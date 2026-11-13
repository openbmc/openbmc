SUMMARY = "GNU Aspell spell-checker"

DESCRIPTION = "Spell checker designed to eventually replace Ispell. \
It can either be used as a library or as an independent spell checker. \
Its main feature is that it does a superior job of suggesting possible \
replacements for a misspelled word than just about any other spell \
checker out there for the English language."

SECTION = "console/utils"

HOMEPAGE = "http://aspell.net/"

LICENSE = "LGPL-2.0-only OR LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"

SRC_URI = "${GNU_MIRROR}/aspell/aspell-${PV}.tar.gz"
SRC_URI[sha256sum] = "0534eae11132405120d11e46bf595bd5dd189bc124ba77a5dd6aae861b48a162"

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
