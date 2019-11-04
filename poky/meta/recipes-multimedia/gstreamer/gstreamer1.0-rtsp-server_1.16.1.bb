SUMMARY = "A library on top of GStreamer for building an RTSP server"
HOMEPAGE = "http://cgit.freedesktop.org/gstreamer/gst-rtsp-server/"
SECTION = "multimedia"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=6762ed442b3822387a51c92d928ead0d"

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base"

PNREAL = "gst-rtsp-server"

SRC_URI = "https://gstreamer.freedesktop.org/src/${PNREAL}/${PNREAL}-${PV}.tar.xz \
           file://0001-introspection.m4-prefix-pkgconfig-paths-with-PKG_CON.patch \
           file://gtk-doc-tweaks.patch \
           "

SRC_URI[md5sum] = "380d6a42e856c32fcefa508ad57129e0"
SRC_URI[sha256sum] = "b0abacad2f86f60d63781d2b24443c5668733e8b08664bbef94124906d700144"

S = "${WORKDIR}/${PNREAL}-${PV}"

inherit autotools pkgconfig upstream-version-is-even gobject-introspection gtk-doc

EXTRA_OECONF = "--disable-examples --disable-tests"

# Starting with 1.8.0 gst-rtsp-server includes dependency-less plugins as well
LIBV = "1.0"
require gst-plugins-package.inc

delete_pkg_m4_file() {
        # This m4 file is out of date and is missing PKG_CONFIG_SYSROOT_PATH tweaks which we need for introspection
        rm "${S}/common/m4/pkg.m4" || true
}

do_configure[prefuncs] += " delete_pkg_m4_file"
