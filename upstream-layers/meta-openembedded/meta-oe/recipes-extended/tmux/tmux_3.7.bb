SUMMARY = "Terminal multiplexer"
HOMEPAGE = "https://github.com/tmux/tmux/wiki"
SECTION = "console/utils"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://tmux.c;beginline=3;endline=17;md5=f256b76d52e7b4d02bf19144bdaca107"

DEPENDS = "ncurses libevent bison-native"

SRC_URI = "https://github.com/tmux/tmux/releases/download/${PV}/tmux-${PV}.tar.gz"
SRC_URI[sha256sum] = "2344f191501b8a73eb71dd6c5fd5dcf8c765f5066f34ab46f04b3013dc7bc1a5"

UPSTREAM_CHECK_URI = "https://github.com/tmux/tmux/releases"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+[a-z]?)"

inherit autotools pkgconfig

PACKAGECONFIG ??= ""
PACKAGECONFIG[utempter] = "ac_cv_header_utempter_h=yes,ac_cv_header_utempter_h=no,libutempter,"
PACKAGECONFIG[sixel] = "--enable-sixel,--disable-sixel"

do_configure:prepend() {
    # The 'compat' directory is needed for output during the build but it's
    # not automatically created when building outside the source directory.
    mkdir -p ${B}/compat
}
