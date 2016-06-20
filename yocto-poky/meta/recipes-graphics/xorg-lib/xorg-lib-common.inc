HOMEPAGE = "http://www.x.org"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=xorg"

SECTION = "x11/libs"
LICENSE = "MIT-X"
DEPENDS = "util-macros"

XORG_PN = "${BPN}"

SRC_URI = "${XORG_MIRROR}/individual/lib/${XORG_PN}-${PV}.tar.bz2"

S = "${WORKDIR}/${XORG_PN}-${PV}"

inherit autotools distro_features_check pkgconfig

EXTRA_OECONF = "--disable-specs --without-groff --without-ps2pdf --without-fop"

PACKAGECONFIG ??= ""
PACKAGECONFIG[xmlto] = "--with-xmlto, --without-xmlto, xmlto-native docbook-xml-dtd4-native docbook-xsl-stylesheets-native"

UNKNOWN_CONFIGURE_WHITELIST += "--enable-malloc0returnsnull --disable-malloc0returnsnull \
                                --disable-specs --without-groff --without-ps2pdf --without-fop \
                                --without-xmlto --with-xmlto \
"

REQUIRED_DISTRO_FEATURES ?= "x11"
REQUIRED_DISTRO_FEATURES_class-native = ""
