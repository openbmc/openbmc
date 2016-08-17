SUMMARY = "Fluendo closed-format GStreamer plug-in"
SECTION = "multimedia"
HOMEPAGE = "https://core.fluendo.com/gstreamer/trac/wiki"
DEPENDS = "gstreamer gst-plugins-base zlib"

inherit autotools pkgconfig

SRC_URI = "http://core.fluendo.com/gstreamer/src/${BPN}/${BPN}-${PV}.tar.bz2"

FILES_${PN} += "${libdir}/gstreamer-0.10/*.so"
FILES_${PN}-dbg += "${libdir}/gstreamer-0.10/.debug"
FILES_${PN}-dev += "${libdir}/gstreamer-0.10/*.la ${libdir}/gstreamer-0.10/*.a"

EXTRA_OECONF = "--disable-valgrind"
