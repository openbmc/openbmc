DESCRIPTION="Xfce Menu Library"
SECTION = "x11/libs"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=04a01abcbdabffae1ca26335a293276b"
DEPENDS = "xfce4-dev-tools-native libxfce4util libxfce4ui intltool-native"

inherit xfce gtk-doc distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += "file://0001-xfce-applications.menu-don-t-bloat-settings-menu-by-.patch"
SRC_URI[md5sum] = "174690ad19243f7ef9529cb0c24b9080"
SRC_URI[sha256sum] = "41c31ba0498c2cc39de4a8e0c2367510adbf4bc2104c17feee358e51e6acf603"

EXTRA_OECONF = "--disable-gtk-doc"

FILES_${PN} += "${datadir}/desktop-directories"
