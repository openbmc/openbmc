DESCRIPTION = "GNU nano (Nano's ANOther editor, or \
Not ANOther editor) is an enhanced clone of the \
Pico text editor."
HOMEPAGE = "http://www.nano-editor.org/"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949"
SECTION = "console/utils"
DEPENDS = "ncurses file"
RDEPENDS_${PN} = "ncurses-terminfo-base"

PV_MAJOR = "${@d.getVar('PV').split('.')[0]}"

SRC_URI = "https://nano-editor.org/dist/v${PV_MAJOR}/nano-${PV}.tar.xz"

SRC_URI[md5sum] = "74196427a09ec2f82a88facd220d2787"
SRC_URI[sha256sum] = "e0a5bca354514e64762c987c200a8758b05e7bcced3b00b3e48ea0a2d383c8a0"

inherit autotools gettext pkgconfig

PACKAGECONFIG[tiny] = "--enable-tiny,"
