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
SRC_URI[md5sum] = "235eaf7d27db3c3679b2054361ff6181"
SRC_URI[sha256sum] = "86bde596a038d6fde619b49d785c0ebf0b3eaa7001a39dbe9316bd5392d221d0"

inherit autotools gettext pkgconfig

PACKAGECONFIG[tiny] = "--enable-tiny,"
