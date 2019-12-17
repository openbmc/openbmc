SUMMARY = "Interactive Connectivity Establishment library"
DESCRIPTION = "Libnice is an implementation of the IETF's draft Interactive Connectivity Establishment standard (ICE)."
HOMEPAGE = "http://nice.freedesktop.org/wiki/"

LICENSE = "LGPLv2.1 & MPLv1.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=9c42325015702feda4f4d2f19a55b767 \
                    file://COPYING.LGPL;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://COPYING.MPL;md5=3c617710e51cdbe0fc00716f056dfb1a \
"

SRC_URI = "http://nice.freedesktop.org/releases/libnice-${PV}.tar.gz"
SRC_URI[md5sum] = "5ad936c43d3c6d33117b2c64982f2fd9"
SRC_URI[sha256sum] = "06b678066f94dde595a4291588ed27acd085ee73775b8c4e8399e28c01eeefdf"

DEPENDS = "glib-2.0 gnutls"

PACKAGECONFIG ??= "gstreamer1.0"
PACKAGECONFIG[gupnp] = "--enable-gupnp,--disable-gupnp,gupnp-igd"
PACKAGECONFIG[gstreamer1.0] = "--with-gstreamer,--without-gstreamer,gstreamer1.0 gstreamer1.0-plugins-base"

inherit autotools pkgconfig gtk-doc gobject-introspection

EXTRA_OECONF += "--without-gstreamer-0.10"

FILES_${PN} += "${libdir}/gstreamer-1.0/*.so"
FILES_${PN}-dev += "${libdir}/gstreamer-1.0/*.la"
FILES_${PN}-staticdev += "${libdir}/gstreamer-1.0/*.a"
FILES_${PN}-dbg += "${libdir}/gstreamer-1.0/.debug"

do_configure_prepend() {
    mkdir ${S}/m4 || true
}

do_compile_append() {
    for i in $(find ${B} -name "*.pc") ; do
        sed -i -e s:${STAGING_DIR_TARGET}::g \
               -e s:/${TARGET_SYS}::g \
                  $i
    done
}
