SUMMARY = "Utilities for managing processes on your system"
DESCRIPTION = "The psmisc package contains utilities for managing processes on your \
system: pstree, killall and fuser.  The pstree command displays a tree \
structure of all of the running processes on your system.  The killall \
command sends a specified signal (SIGTERM if nothing is specified) to \
processes identified by name.  The fuser command identifies the PIDs \
of processes that are using specified files or filesystems."
SECTION = "base"
DEPENDS = "ncurses virtual/libintl"
LICENSE = "GPLv2"

SRC_URI = "${SOURCEFORGE_MIRROR}/psmisc/psmisc-${PV}.tar.gz"

S = "${WORKDIR}/psmisc-${PV}"

inherit autotools gettext

ALLOW_EMPTY_${PN} = "1"

PACKAGES =+ "fuser fuser-doc killall killall-doc pstree pstree-doc"
PACKAGES += "psmisc-extras"

FILES_${PN} = ""
RDEPENDS_${PN} = "fuser killall pstree"

FILES_fuser = "${bindir}/fuser.${BPN}"
FILES_fuser-doc = "${mandir}/man1/fuser*"

FILES_killall = "${bindir}/killall.${BPN}"
FILES_killall-doc = "${mandir}/man1/killall*"

FILES_pstree = "${bindir}/pstree"
FILES_pstree-doc = "${mandir}/man1/pstree*"

FILES_psmisc-extras = "${bindir}"
FILES_psmisc-extras-doc = "${mandir}"

inherit update-alternatives

ALTERNATIVE_PRIORITY = "90"

ALTERNATIVE_killall = "killall"

ALTERNATIVE_fuser = "fuser"
