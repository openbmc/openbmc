SUMMARY = "Utilities for managing processes on your system"
HOMEPAGE = "http://psmisc.sf.net/"
DESCRIPTION = "The psmisc package contains utilities for managing processes on your \
system: pstree, killall and fuser.  The pstree command displays a tree \
structure of all of the running processes on your system.  The killall \
command sends a specified signal (SIGTERM if nothing is specified) to \
processes identified by name.  The fuser command identifies the PIDs \
of processes that are using specified files or filesystems."
SECTION = "base"
DEPENDS = "ncurses virtual/libintl"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI = "git://gitlab.com/psmisc/psmisc.git;protocol=https;branch=master \
           "
SRCREV = "9091d6dbcce3d8fb87adf9249a2eb346d25a562c"
S = "${WORKDIR}/git"

inherit autotools gettext

# Upstream has a custom autogen.sh which invokes po/update-potfiles as they 
# don't ship a po/POTFILES.in (which is silly).  Without that file gettext 
# doesn't believe po/ is a gettext directory and won't generate po/Makefile.
do_configure:prepend() {
    ( cd ${S} && po/update-potfiles )
}


PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
PACKAGECONFIG[selinux] = "--enable-selinux,--disable-selinux,libselinux"

ALLOW_EMPTY:${PN} = "1"

PACKAGES =+ "fuser fuser-doc killall killall-doc pstree pstree-doc"
PACKAGES += "psmisc-extras"

FILES:${PN} = ""
RDEPENDS:${PN} = "fuser killall pstree"

FILES:fuser = "${bindir}/fuser.${BPN}"
FILES:fuser-doc = "${mandir}/man1/fuser*"

FILES:killall = "${bindir}/killall.${BPN}"
FILES:killall-doc = "${mandir}/man1/killall*"

FILES:pstree = "${bindir}/pstree"
FILES:pstree-doc = "${mandir}/man1/pstree*"

FILES:psmisc-extras = "${bindir}"
FILES:psmisc-extras-doc = "${mandir}"

inherit update-alternatives

ALTERNATIVE_PRIORITY = "90"

ALTERNATIVE:killall = "killall"

ALTERNATIVE:fuser = "fuser"

ALTERNATIVE:pstree = "pstree"
