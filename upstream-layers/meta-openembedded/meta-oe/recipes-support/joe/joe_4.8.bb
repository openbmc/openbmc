SECTION = "console/utils"
SUMMARY = "Console text editor with good functionality, good choice for vi-haters"
HOMEPAGE = "http://joe-editor.sourceforge.net/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = " \
    ${SOURCEFORGE_MIRROR}/joe-editor/joe-${PV}.tar.gz \
    file://0001-Fix-assign-to-stdin-use-freopen-instead-issue.patch \
"

PACKAGECONFIG ??= "curses"
PACKAGECONFIG[curses] = "--enable-curses,--disable-curses,ncurses,ncurses-terminfo"

inherit autotools-brokensep mime-xdg

SRC_URI[sha256sum] = "6995b28ee20dcdbbcb5a45a4c110642dc96d67748aea27450c74cdb4dd07cc20"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/joe-editor/files/JOE%20sources/"
UPSTREAM_CHECK_REGEX = "joe-(?P<pver>\d+(\.\d+)+)/"

