SUMMARY = "X driver"
HOMEPAGE = "http://www.x.org"
BUGTRACKER = "https://bugs.freedesktop.org"
SECTION = "x11/drivers"
LICENSE = "MIT"

PE = "2"

DEPENDS = "virtual/xserver xorgproto util-macros"

XORG_DRIVER_COMPRESSOR ?= ".tar.bz2"
SRC_URI = "${XORG_MIRROR}/individual/driver/${BPN}-${PV}${XORG_DRIVER_COMPRESSOR}"

FILES:${PN} += " ${libdir}/xorg/modules/drivers/*.so"

inherit pkgconfig features_check
XORGBUILDCLASS ??= "autotools"
inherit_defer ${XORGBUILDCLASS}

# depends on virtual/xserver
REQUIRED_DISTRO_FEATURES = "x11"

SECURITY_LDFLAGS = "${SECURITY_X_LDFLAGS}"

require recipes-graphics/xorg-xserver/xserver-abi.inc
