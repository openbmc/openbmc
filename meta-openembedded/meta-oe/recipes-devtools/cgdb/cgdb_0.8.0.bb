SUMMARY = "curses-based interface to GDB"
DESCRIPTION = "cgdb is a lightweight curses (terminal-based) interface to the GNU Debugger (GDB)."
HOMEPAGE = "http://cgdb.github.io/"
SECTION = "devel"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "flex-native readline ncurses"

inherit autotools texinfo

SRC_URI = "http://cgdb.me/files/${BP}.tar.gz \
           file://0001-cgdb-Do-not-search-for-cgdb.txt-in-build-dir.patch"
SRC_URI[sha256sum] = "0d38b524d377257b106bad6d856d8ae3304140e1ee24085343e6ddf1b65811f1"

CACHED_CONFIGUREVARS = "ac_cv_file__dev_ptmx=yes ac_cv_rl_version=6.2 ac_cv_file__proc_self_status=yes"
EXTRA_OECONF = "--with-readline=${STAGING_LIBDIR} \
    --with-ncurses=${STAGING_LIBDIR}"

RDEPENDS:${PN} = "gdb"
