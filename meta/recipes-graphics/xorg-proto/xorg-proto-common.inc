SUMMARY = "X protocol headers: ${XORG_PN}"
HOMEPAGE = "http://www.x.org"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=xorg"

SECTION = "x11/libs"
LICENSE = "MIT-X"

XORG_PN = "${BPN}"

SRC_URI = "${XORG_MIRROR}/individual/proto/${XORG_PN}-${PV}.tar.bz2"

S = "${WORKDIR}/${XORG_PN}-${PV}"

DEPENDS = "util-macros"

inherit autotools pkgconfig
EXTRA_OECONF = "--without-fop"

UNKNOWN_CONFIGURE_WHITELIST += "--without-fop --without-xmlto --with-xmlto"

PACKAGECONFIG ??= ""
PACKAGECONFIG[xmlto] = "--with-xmlto, --without-xmlto, xmlto-native docbook-xml-dtd4-native docbook-xsl-stylesheets-native"

# ${PN} is empty so we need to tweak -dev and -dbg package dependencies
RDEPENDS_${PN}-dev = ""
RRECOMMENDS_${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"
