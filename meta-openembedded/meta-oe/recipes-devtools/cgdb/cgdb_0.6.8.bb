SUMMARY = "curses-based interface to GDB"
DESCRIPTION = "cgdb is a lightweight curses (terminal-based) interface to the GNU Debugger (GDB)."
HOMEPAGE = "http://cgdb.github.io/"
SECTION = "devel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "flex-native readline ncurses"

inherit autotools

SRC_URI = "http://cgdb.me/files/${BP}.tar.gz \
    file://remove-help2man.patch"
SRC_URI[md5sum] = "7bd38c79bf4d794d239928fef401fca3"
SRC_URI[sha256sum] = "be203e29be295097439ab67efe3dc8261f742c55ff3647718d67d52891f4cf41"

CACHED_CONFIGUREVARS = "ac_cv_file__dev_ptmx=yes ac_cv_rl_version=6.2"
EXTRA_OECONF = "--with-readline=${STAGING_LIBDIR} \
    --with-ncurses=${STAGING_LIBDIR}"

RDEPENDS_${PN} = "gdb"
