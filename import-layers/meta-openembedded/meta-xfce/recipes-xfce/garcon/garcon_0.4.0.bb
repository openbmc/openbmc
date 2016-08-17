DESCRIPTION="Xfce Menu Library"
SECTION = "x11/libs"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=04a01abcbdabffae1ca26335a293276b"
DEPENDS = "xfce4-dev-tools-native libxfce4util libxfce4ui intltool-native"

inherit xfce gtk-doc

SRC_URI += "file://0001-xfce-applications.menu-don-t-bloat-settings-menu-by-.patch"
SRC_URI[md5sum] = "aba62b80787aac295083bf7afd419ffb"
SRC_URI[sha256sum] = "787dc859713b6518992d760d4b858fb02e8a30705e6f6e871d7a14cc97bca972"

FILES_${PN} += "${datadir}/desktop-directories"
