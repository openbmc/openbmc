SUMMARY = "Terminal multiplexer"
HOMEPAGE = "http://tmux.sourceforge.net"
SECTION = "console/utils"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://tmux.c;beginline=3;endline=17;md5=8685b4455330a940fab1ff451aa941a0"

DEPENDS = "ncurses libevent"


SRC_URI = "git://github.com/tmux/tmux.git;branch=master"
SRCREV ?= "310f0a960ca64fa3809545badc629c0c166c6cd2"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

inherit autotools pkgconfig

PACKAGECONFIG ??= ""
PACKAGECONFIG[utempter] = "ac_cv_header_utempter_h=yes,ac_cv_header_utempter_h=no,libutempter,"

