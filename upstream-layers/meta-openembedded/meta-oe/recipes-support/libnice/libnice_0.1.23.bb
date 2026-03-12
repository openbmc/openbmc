SUMMARY = "Interactive Connectivity Establishment library"
DESCRIPTION = "Libnice is an implementation of the IETF's draft Interactive Connectivity Establishment standard (ICE)."
HOMEPAGE = "http://nice.freedesktop.org/wiki/"

LICENSE = "LGPL-2.1-only & MPL-1.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=963cc62096d5412ee9580043babb8b60 \
                    file://COPYING.LGPL;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://COPYING.MPL;md5=3c617710e51cdbe0fc00716f056dfb1a \
                    "

SRC_URI = "http://nice.freedesktop.org/releases/${BP}.tar.gz"
SRC_URI[sha256sum] = "618fc4e8de393b719b1641c1d8eec01826d4d39d15ade92679d221c7f5e4e70d"

UPSTREAM_CHECK_URI = "https://gitlab.freedesktop.org/libnice/libnice/-/tags"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

DEPENDS = "glib-2.0 gnutls ${@bb.utils.contains('DISTRO_FEATURES', 'api-documentation', 'graphviz-native', '', d)}"

PACKAGECONFIG[gupnp] = "-Dgupnp=enabled,-Dgupnp=disabled,gupnp"
PACKAGECONFIG[gstreamer] = "-Dgstreamer=enabled,-Dgstreamer=disabled,gstreamer1.0"
PACKAGECONFIG[introspection] = "-Dintrospection=enabled,-Dintrospection=disabled,"

GTKDOC_MESON_OPTION = "gtk_doc"
GTKDOC_MESON_ENABLE_FLAG = "enabled"
GTKDOC_MESON_DISABLE_FLAG = "disabled"

inherit meson gtk-doc gobject-introspection

EXTRA_OEMESON = "-Dexamples=disabled -Dtests=disabled"

FILES:${PN} += "${libdir}/gstreamer-1.0/*.so"
FILES:${PN}-staticdev += "${libdir}/gstreamer-1.0/*.a ${libdir}/gstreamer-1.0/pkgconfig"
FILES:${PN}-dbg += "${libdir}/gstreamer-1.0/.debug"
