DESCRIPTION = "Xfce Menu Library"
SECTION = "x11/libs"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=04a01abcbdabffae1ca26335a293276b"
DEPENDS = "xfce4-dev-tools-native libxfce4ui intltool-native"

inherit xfce gtk-doc features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += "file://0001-xfce-applications.menu-don-t-bloat-settings-menu-by-.patch"
SRC_URI[md5sum] = "2964c7a7e5d4aac58b4afef9b8602914"
SRC_URI[sha256sum] = "82c3b61b508011642b09e6fb01b1d3f22c4e4de0fc54a9244327d0ddb66b2423"

EXTRA_OECONF = "--disable-gtk-doc"

FILES_${PN} += "${datadir}/desktop-directories"
