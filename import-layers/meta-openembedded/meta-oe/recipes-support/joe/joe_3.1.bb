SECTION = "console/utils"
SUMMARY = "Console text editor with good functionality, good choice for vi-haters"
HOMEPAGE = "http://joe-editor.sourceforge.net/"
LICENSE = "GPLv1"
LIC_FILES_CHKSUM = "file://COPYING;md5=da10ed7cf8038981c580e11c1d3e8fb6"

SRC_URI = "${SOURCEFORGE_MIRROR}/joe-editor/joe-${PV}.tar.gz"

PACKAGECONFIG ??= "curses"
PACKAGECONFIG[curses] = "--enable-curses,--disable-curses,ncurses,ncurses-terminfo"

inherit autotools

SRC_URI[md5sum] = "2a6ef018870fca9b7df85401994fb0e0"
SRC_URI[sha256sum] = "252390e4bc687957f09f334095904c8cc53b39c7b663ed47861ae1d11aef5946"
