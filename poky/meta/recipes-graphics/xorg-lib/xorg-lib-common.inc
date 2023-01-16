HOMEPAGE = "http://www.x.org"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=xorg"

SECTION = "x11/libs"
LICENSE = "MIT"
DEPENDS = "util-macros"

XORG_PN = "${BPN}"
XORG_EXT ?= "tar.xz"

SRC_URI = "${XORG_MIRROR}/individual/lib/${XORG_PN}-${PV}.${XORG_EXT}"

S = "${WORKDIR}/${XORG_PN}-${PV}"

inherit autotools features_check pkgconfig

EXTRA_OECONF = "--disable-specs --without-groff --without-ps2pdf --without-fop"

PACKAGECONFIG ??= ""
PACKAGECONFIG[xmlto] = "--with-xmlto, --without-xmlto, xmlto-native docbook-xml-dtd4-native docbook-xsl-stylesheets-native"

UNKNOWN_CONFIGURE_OPT_IGNORE += "--enable-malloc0returnsnull --disable-malloc0returnsnull \
                                --disable-specs --without-groff --without-ps2pdf --without-fop \
                                --without-xmlto --with-xmlto \
"

REQUIRED_DISTRO_FEATURES ?= "x11"
