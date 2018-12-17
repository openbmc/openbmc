SUMMARY = "Interactive Connectivity Establishment library"
DESCRIPTION = "Libnice is an implementation of the IETF's draft Interactive Connectivity Establishment standard (ICE)."
HOMEPAGE = "http://nice.freedesktop.org/wiki/"
SRC_URI = "http://nice.freedesktop.org/releases/libnice-${PV}.tar.gz"
SRC_URI[md5sum] = "c9b9b74b8ae1b3890e4bd93f1b70e8ff"
SRC_URI[sha256sum] = "be120ba95d4490436f0da077ffa8f767bf727b82decf2bf499e39becc027809c"

LICENSE = "LGPLv2.1 & MPLv1.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=9c42325015702feda4f4d2f19a55b767 \
                    file://COPYING.LGPL;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://COPYING.MPL;md5=3c617710e51cdbe0fc00716f056dfb1a \
"

DEPENDS = "glib-2.0 gnutls"

PACKAGECONFIG ??= "gstreamer1.0"
PACKAGECONFIG[gupnp] = "--enable-gupnp,--disable-gupnp,gupnp-igd"
PACKAGECONFIG[gstreamer0.10] = "--with-gstreamer-0.10,--without-gstreamer-0.10,gstreamer gst-plugins-base"
PACKAGECONFIG[gstreamer1.0] = "--with-gstreamer,--without-gstreamer,gstreamer1.0 gstreamer1.0-plugins-base"

inherit autotools pkgconfig gtk-doc gobject-introspection

FILES_${PN} += "${libdir}/gstreamer-0.10/*.so ${libdir}/gstreamer-1.0/*.so"
FILES_${PN}-dev += "${libdir}/gstreamer-0.10/*.la ${libdir}/gstreamer-1.0/*.la"
FILES_${PN}-staticdev += "${libdir}/gstreamer-0.10/*.a ${libdir}/gstreamer-1.0/*.a"
FILES_${PN}-dbg += "${libdir}/gstreamer-0.10/.debug ${libdir}/gstreamer-1.0/.debug"

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
