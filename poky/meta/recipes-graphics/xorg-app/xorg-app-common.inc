SUMMARY = "X application"
HOMEPAGE = "http://www.x.org/"
BUGTRACKER = "https://bugs.freedesktop.org"
SECTION = "x11/apps"
LICENSE = "MIT"
DEPENDS = "util-macros-native virtual/libx11"

# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI_EXT = "bz2"
SRC_URI = "${XORG_MIRROR}/individual/app/${BPN}-${PV}.tar.${SRC_URI_EXT}"

inherit autotools pkgconfig features_check

FILES:${PN} += " ${libdir}/X11/${BPN} ${datadir}/X11/app-defaults/"
