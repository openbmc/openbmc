SUMMARY = "curses-based interface to GDB"
DESCRIPTION = "cgdb is a lightweight curses (terminal-based) interface to the GNU Debugger (GDB)."
HOMEPAGE = "http://cgdb.github.io/"
SECTION = "devel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "flex-native readline ncurses"

inherit autotools texinfo

SRC_URI = "http://cgdb.me/files/${BP}.tar.gz \
           file://0001-Avoid-use-of-mips-which-is-reserved-on-mips.patch \
"
SRC_URI[md5sum] = "a104862ffd3145b076303992e9a3af26"
SRC_URI[sha256sum] = "bb723be58ec68cb59a598b8e24a31d10ef31e0e9c277a4de07b2f457fe7de198"

CACHED_CONFIGUREVARS = "ac_cv_file__dev_ptmx=yes ac_cv_rl_version=6.2 ac_cv_file__proc_self_status=yes"
EXTRA_OECONF = "--with-readline=${STAGING_LIBDIR} \
    --with-ncurses=${STAGING_LIBDIR}"

RDEPENDS_${PN} = "gdb"
