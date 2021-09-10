SUMMARY = "Terminal multiplexer"
HOMEPAGE = "http://tmux.sourceforge.net"
SECTION = "console/utils"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://tmux.c;beginline=3;endline=17;md5=f256b76d52e7b4d02bf19144bdaca107"

DEPENDS = "ncurses libevent"

SRC_URI = "https://github.com/tmux/tmux/releases/download/${PV}/tmux-${PV}.tar.gz"
SRC_URI[md5sum] = "7301d3fa12f35051edb185496766c745"
SRC_URI[sha256sum] = "918f7220447bef33a1902d4faff05317afd9db4ae1c9971bef5c787ac6c88386"

UPSTREAM_CHECK_URI = "https://github.com/tmux/tmux/releases"

inherit autotools pkgconfig

PACKAGECONFIG ??= ""
PACKAGECONFIG[utempter] = "ac_cv_header_utempter_h=yes,ac_cv_header_utempter_h=no,libutempter,"

do_configure:prepend() {
    # The 'compat' directory is needed for output during the build but it's
    # not automatically created when building outside the source directory.
    mkdir -p ${B}/compat
}
