SUMMARY = "Interactive Connectivity Establishment library"
DESCRIPTION = "Libnice is an implementation of the IETF's draft Interactive Connectivity Establishment standard (ICE)."
HOMEPAGE = "http://nice.freedesktop.org/wiki/"

LICENSE = "LGPL-2.1-only & MPL-1.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=9c42325015702feda4f4d2f19a55b767 \
                    file://COPYING.LGPL;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://COPYING.MPL;md5=3c617710e51cdbe0fc00716f056dfb1a \
"

SRC_URI = "http://nice.freedesktop.org/releases/libnice-${PV}.tar.gz \
           file://0001-agent-meson-Replace-filename-with-basename.patch"
SRC_URI[sha256sum] = "72e73a2acf20f59093e21d5601606e405873503eb35f346fa621de23e99b3b39"

DEPENDS = "glib-2.0 gnutls ${@bb.utils.contains('DISTRO_FEATURES', 'api-documentation', 'graphviz-native', '', d)}"

PACKAGECONFIG[gupnp] = "-Dgupnp=enabled,-Dgupnp=disabled,gupnp"
PACKAGECONFIG[gstreamer] = "-Dgstreamer=enabled,-Dgstreamer=disabled,gstreamer1.0"
PACKAGECONFIG[introspection] = "-Dintrospection=enabled,-Dintrospection=disabled,"

EXTRA_OEMESON = "-Dgstreamer=disabled"

GTKDOC_MESON_OPTION = "gtk_doc"
GTKDOC_MESON_ENABLE_FLAG = "enabled"
GTKDOC_MESON_DISABLE_FLAG = "disabled"

inherit meson gtk-doc gobject-introspection

FILES:${PN} += "${libdir}/gstreamer-1.0/*.so"
FILES:${PN}-dev += "${libdir}/gstreamer-1.0/*.la"
FILES:${PN}-staticdev += "${libdir}/gstreamer-1.0/*.a"
FILES:${PN}-dbg += "${libdir}/gstreamer-1.0/.debug"

do_configure:prepend() {
    mkdir ${S}/m4 || true
}

do_compile:append() {
    for i in $(find ${B} -name "*.pc") ; do
        sed -i -e s:${STAGING_DIR_TARGET}::g \
               -e s:/${TARGET_SYS}::g \
                  $i
    done
}
