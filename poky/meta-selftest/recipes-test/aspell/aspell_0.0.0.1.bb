# This recipe is a copy from the oe-core one.
# It has a lower and invalid version number in order not to be accidentally used by bitbake.
# It is used for tests that require overlayed recipe files.

SUMMARY = "GNU Aspell spell-checker"
SECTION = "console/utils"
HOMEPAGE = "https://ftp.gnu.org/gnu/aspell/"

LICENSE = "LGPL-2.0-only | LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"

SRC_URI = "${GNU_MIRROR}/aspell/aspell-${PV}.tar.gz"
SRC_URI[md5sum] = "e66a9c9af6a60dc46134fdacf6ce97d7"
SRC_URI[sha256sum] = "f52583a83a63633701c5f71db3dc40aab87b7f76b29723aeb27941eff42df6e1"

EXCLUDE_FROM_WORLD = "1"

PACKAGECONFIG ??= ""
PACKAGECONFIG[curses] = "--enable-curses,--disable-curses,ncurses"

PACKAGES += "libaspell libpspell libpspell-dev aspell-utils"

FILES:${PN}-dbg += "${libdir}/aspell-0.60/.debu*"
FILES:libaspell = "${libdir}/libaspell.so.* ${libdir}/aspell*"
FILES:aspell-utils = "${bindir}/word-list-compress ${bindir}/aspell-import ${bindir}/run-with-aspell ${bindir}/pre*"
FILES:${PN} = "${bindir}/aspell"
FILES:libpspell = "${libdir}/libpspell.so.*"
FILES:libpspell-dev = "${libdir}/libpspell* ${bindir}/pspell-config ${includedir}/pspell"

ARM_INSTRUCTION_SET:armv4 = "arm"
ARM_INSTRUCTION_SET:armv5 = "arm"
ARM_INSTRUCTION_SET:armv6 = "arm"

inherit autotools gettext
