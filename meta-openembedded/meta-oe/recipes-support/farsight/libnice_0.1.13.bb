SUMMARY = "Interactive Connectivity Establishment library"
DESCRIPTION = "Libnice is an implementation of the IETF's draft Interactive Connectivity Establishment standard (ICE)."
HOMEPAGE = "http://nice.freedesktop.org/wiki/"
SRC_URI = "http://nice.freedesktop.org/releases/libnice-${PV}.tar.gz"
SRC_URI[md5sum] = "3226faeaf48a9150ada00da2e2865959"
SRC_URI[sha256sum] = "61112d9f3be933a827c8365f20551563953af6718057928f51f487bfe88419e1"

LICENSE = "LGPLv2.1 & MPLv1.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=9c42325015702feda4f4d2f19a55b767 \
                    file://COPYING.LGPL;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://COPYING.MPL;md5=3c617710e51cdbe0fc00716f056dfb1a \
"

DEPENDS = "glib-2.0 gstreamer1.0 gstreamer1.0-plugins-base"

PACKAGECONFIG ??= ""
PACKAGECONFIG[gupnp] = "--enable-gupnp,--disable-gupnp,gupnp-igd"

inherit autotools pkgconfig gtk-doc gobject-introspection

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
