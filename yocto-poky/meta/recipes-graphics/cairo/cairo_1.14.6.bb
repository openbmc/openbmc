require cairo.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=e73e999e0c72b5ac9012424fa157ad77"

SRC_URI = "http://cairographics.org/releases/cairo-${PV}.tar.xz"

SRC_URI[md5sum] = "23a0b2f0235431d35238df1d3a517fdb"
SRC_URI[sha256sum] = "613cb38447b76a93ff7235e17acd55a78b52ea84a9df128c3f2257f8eaa7b252"

UPSTREAM_CHECK_REGEX = "cairo-(?P<pver>\d+(\.\d+)+)\.tar"

PACKAGES =+ "cairo-gobject cairo-script-interpreter cairo-perf-utils"

SUMMARY_${PN} = "The Cairo 2D vector graphics library"
DESCRIPTION_${PN} = "Cairo is a multi-platform library providing anti-aliased \
vector-based rendering for multiple target backends. Paths consist \
of line segments and cubic splines and can be rendered at any width \
with various join and cap styles. All colors may be specified with \
optional translucence (opacity/alpha) and combined using the \
extended Porter/Duff compositing algebra as found in the X Render \
Extension."

SUMMARY_cairo-gobject = "The Cairo library GObject wrapper library"
DESCRIPTION_cairo-gobject = "A GObject wrapper library for the Cairo API."

SUMMARY_cairo-script-interpreter = "The Cairo library script interpreter"
DESCRIPTION_cairo-script-interpreter = "The Cairo script interpreter implements \
CairoScript.  CairoScript is used by tracing utilities to enable the ability \
to replay rendering."

DESCRIPTION_cairo-perf-utils = "The Cairo library performance utilities"

FILES_${PN} = "${libdir}/libcairo.so.*"
FILES_${PN}-dev += "${libdir}/cairo/*.so"
FILES_${PN}-gobject = "${libdir}/libcairo-gobject.so.*"
FILES_${PN}-script-interpreter = "${libdir}/libcairo-script-interpreter.so.*"
FILES_${PN}-perf-utils = "${bindir}/cairo-trace ${libdir}/cairo/*.la ${libdir}/cairo/libcairo-trace.so.*"

do_install_append () {
	rm -rf ${D}${bindir}/cairo-sphinx
	rm -rf ${D}${libdir}/cairo/cairo-fdr*
	rm -rf ${D}${libdir}/cairo/cairo-sphinx*
	rm -rf ${D}${libdir}/cairo/.debug/cairo-fdr*
	rm -rf ${D}${libdir}/cairo/.debug/cairo-sphinx*
}
