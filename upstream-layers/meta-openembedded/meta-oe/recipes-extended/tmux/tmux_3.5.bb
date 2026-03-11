SUMMARY = "Terminal multiplexer"
HOMEPAGE = "http://tmux.sourceforge.net"
SECTION = "console/utils"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://tmux.c;beginline=3;endline=17;md5=f256b76d52e7b4d02bf19144bdaca107"

DEPENDS = "ncurses libevent bison-native"

SRC_URI = "https://github.com/tmux/tmux/releases/download/${PV}/tmux-${PV}.tar.gz"
SRC_URI[sha256sum] = "2fe01942e7e7d93f524a22f2c883822c06bc258a4d61dba4b407353d7081950f"

UPSTREAM_CHECK_URI = "https://github.com/tmux/tmux/releases"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

inherit autotools pkgconfig

PACKAGECONFIG ??= ""
PACKAGECONFIG[utempter] = "ac_cv_header_utempter_h=yes,ac_cv_header_utempter_h=no,libutempter,"

do_configure:prepend() {
    # The 'compat' directory is needed for output during the build but it's
    # not automatically created when building outside the source directory.
    mkdir -p ${B}/compat
}
