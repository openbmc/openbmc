# This recipe is a copy from the oe-core one.
# It has a lower and invalid version number in order not to be accidentally used by bitbake.
# It is used for tests that require overlayed recipe files.

SUMMARY = "GNU Aspell spell-checker"
SECTION = "console/utils"

LICENSE = "LGPLv2 | LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"

PR = "r1"

SRC_URI = "${GNU_MIRROR}/aspell/aspell-${PV}.tar.gz"
SRC_URI[md5sum] = "e66a9c9af6a60dc46134fdacf6ce97d7"
SRC_URI[sha256sum] = "f52583a83a63633701c5f71db3dc40aab87b7f76b29723aeb27941eff42df6e1"

PACKAGECONFIG ??= ""
PACKAGECONFIG[curses] = "--enable-curses,--disable-curses,ncurses"

PACKAGES += "libaspell libpspell libpspell-dev aspell-utils"

FILES_${PN}-dbg += "${libdir}/aspell-0.60/.debu*"
FILES_libaspell = "${libdir}/libaspell.so.* ${libdir}/aspell*"
FILES_aspell-utils = "${bindir}/word-list-compress ${bindir}/aspell-import ${bindir}/run-with-aspell ${bindir}/pre*"
FILES_${PN} = "${bindir}/aspell"
FILES_libpspell = "${libdir}/libpspell.so.*"
FILES_libpspell-dev = "${libdir}/libpspell* ${bindir}/pspell-config ${includedir}/pspell"

ARM_INSTRUCTION_SET = "arm"
inherit autotools gettext
