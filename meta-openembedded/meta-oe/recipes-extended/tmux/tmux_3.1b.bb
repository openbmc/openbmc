SUMMARY = "Terminal multiplexer"
HOMEPAGE = "http://tmux.sourceforge.net"
SECTION = "console/utils"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://tmux.c;beginline=3;endline=17;md5=f256b76d52e7b4d02bf19144bdaca107"

DEPENDS = "ncurses libevent"

SRC_URI = "https://github.com/tmux/tmux/releases/download/${PV}/tmux-${PV}.tar.gz"
SRC_URI[md5sum] = "2cdacbf06ac4980d3f5312ad23b37f9b"
SRC_URI[sha256sum] = "d93f351d50af05a75fe6681085670c786d9504a5da2608e481c47cf5e1486db9"

UPSTREAM_CHECK_URI = "https://github.com/tmux/tmux/releases"

inherit autotools pkgconfig

PACKAGECONFIG ??= ""
PACKAGECONFIG[utempter] = "ac_cv_header_utempter_h=yes,ac_cv_header_utempter_h=no,libutempter,"

do_configure_prepend() {
    # The 'compat' directory is needed for output during the build but it's
    # not automatically created when building outside the source directory.
    mkdir -p ${B}/compat
}
