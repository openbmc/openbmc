DESCRIPTION="Xfce Menu Library"
SECTION = "x11/libs"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=04a01abcbdabffae1ca26335a293276b"
DEPENDS = "xfce4-dev-tools-native libxfce4util libxfce4ui intltool-native"

inherit xfce gtk-doc distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += "file://0001-xfce-applications.menu-don-t-bloat-settings-menu-by-.patch"
SRC_URI[md5sum] = "a4b6332ff6f090575d534d6a9d2761d8"
SRC_URI[sha256sum] = "a87f09648ff10d45c524b3bfe618f18622bdd8b205589d35fed2f12d9c79c47c"

EXTRA_OECONF = "--disable-gtk-doc"

FILES_${PN} += "${datadir}/desktop-directories"
