SUMMARY = "GNU Aspell spell-checker"
SECTION = "console/utils"

LICENSE = "LGPLv2 | LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"

PR = "r1"

SRC_URI = "${GNU_MIRROR}/aspell/aspell-${PV}.tar.gz"
SRC_URI[md5sum] = "8ef2252609c511cd2bb26f3a3932ef28"
SRC_URI[sha256sum] = "5ca8fc8cb0370cc6c9eb5b64c6d1bc5d57b3750dbf17887726c3407d833b70e4"

PACKAGECONFIG ??= ""
PACKAGECONFIG[curses] = "--enable-curses,--disable-curses,ncurses"

PACKAGES += "libaspell libpspell aspell-utils"

RDEPENDS_${PN}-utils += "perl"

FILES_libaspell = "${libdir}/libaspell.so.* ${libdir}/aspell*"
FILES_aspell-utils = "${bindir}/word-list-compress ${bindir}/aspell-import ${bindir}/run-with-aspell ${bindir}/pre*"
FILES_${PN} = "${bindir}/aspell"
FILES_libpspell = "${libdir}/libpspell.so.*"
FILES_${PN}-dev += "${bindir}/pspell-config"

ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"
ARM_INSTRUCTION_SET_armv6 = "arm"

inherit autotools-brokensep gettext texinfo binconfig-disabled

BINCONFIG = "${bindir}/pspell-config"
