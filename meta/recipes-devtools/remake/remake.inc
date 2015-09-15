SUMMARY = "Makefile debugger which is gnumake compatible"
DESCRIPTION = "remake is a patched and modernized version of GNU make \
utility that adds improved error reporting, the ability to trace \
execution in a comprehensible way, and a debugger."

HOMEPAGE = "http://bashdb.sourceforge.net/remake/"
SECTION = "devel"

SRC_URI = "git://github.com/rocky/remake.git"

inherit autotools gettext update-alternatives pkgconfig

ALTERNATIVE_${PN} = "make"
ALTERNATIVE_LINK_NAME[make] = "${bindir}/make"
ALTERNATIVE_TARGET[make] = "${bindir}/remake"
ALTERNATIVE_PRIORITY = "100"
