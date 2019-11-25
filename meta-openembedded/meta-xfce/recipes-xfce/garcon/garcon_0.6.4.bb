DESCRIPTION = "Xfce Menu Library"
SECTION = "x11/libs"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=04a01abcbdabffae1ca26335a293276b"
DEPENDS = "xfce4-dev-tools-native libxfce4ui intltool-native"

inherit xfce gtk-doc features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += "file://0001-xfce-applications.menu-don-t-bloat-settings-menu-by-.patch"
SRC_URI[md5sum] = "9715aa8214be9c553d9b8c44fde90b9f"
SRC_URI[sha256sum] = "d75e4753037a74733c07b71b8db7a656d869869f0f107f6411a306bbc87a762d"

EXTRA_OECONF = "--disable-gtk-doc"

FILES_${PN} += "${datadir}/desktop-directories"
