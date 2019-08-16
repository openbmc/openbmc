DESCRIPTION = "GNU nano (Nano's ANOther editor, or \
Not ANOther editor) is an enhanced clone of the \
Pico text editor."
HOMEPAGE = "http://www.nano-editor.org/"
SECTION = "console/utils"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949"

DEPENDS = "ncurses file"
RDEPENDS_${PN} = "ncurses-terminfo-base"

PV_MAJOR = "${@d.getVar('PV').split('.')[0]}"

SRC_URI = "https://nano-editor.org/dist/v${PV_MAJOR}/nano-${PV}.tar.xz"
SRC_URI[md5sum] = "23f4f7b5c0d1f04ad555960dc294f2b8"
SRC_URI[sha256sum] = "00d3ad1a287a85b4bf83e5f06cedd0a9f880413682bebd52b4b1e2af8cfc0d81"

inherit autotools gettext pkgconfig

PACKAGECONFIG[tiny] = "--enable-tiny,"
