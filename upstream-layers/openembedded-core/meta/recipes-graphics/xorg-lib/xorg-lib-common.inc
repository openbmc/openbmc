HOMEPAGE = "http://www.x.org"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=xorg"

SECTION = "x11/libs"
LICENSE = "MIT"
DEPENDS = "util-macros-native"

XORG_PN = "${BPN}"
XORG_EXT ?= "tar.xz"

SRC_URI = "${XORG_MIRROR}/individual/lib/${XORG_PN}-${PV}.${XORG_EXT}"

S = "${UNPACKDIR}/${XORG_PN}-${PV}"

XORGBUILDCLASS ??= "autotools"
inherit_defer ${XORGBUILDCLASS}

inherit features_check pkgconfig

EXTRA_OECONF = "--disable-docs --disable-specs"
UNKNOWN_CONFIGURE_OPT_IGNORE += "--disable-docs --disable-specs"

REQUIRED_DISTRO_FEATURES ?= "x11"
