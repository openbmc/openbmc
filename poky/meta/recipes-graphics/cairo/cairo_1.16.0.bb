SUMMARY = "The Cairo 2D vector graphics library"
DESCRIPTION = "Cairo is a multi-platform library providing anti-aliased \
vector-based rendering for multiple target backends. Paths consist \
of line segments and cubic splines and can be rendered at any width \
with various join and cap styles. All colors may be specified with \
optional translucence (opacity/alpha) and combined using the \
extended Porter/Duff compositing algebra as found in the X Render \
Extension."
HOMEPAGE = "http://cairographics.org"
BUGTRACKER = "http://bugs.freedesktop.org"
SECTION = "libs"

LICENSE = "(MPL-1.1 | LGPLv2.1) & GPLv3+"
LICENSE_${PN} = "MPL-1.1 | LGPLv2.1"
LICENSE_${PN}-dev = "MPL-1.1 | LGPLv2.1"
LICENSE_${PN}-doc = "MPL-1.1 | LGPLv2.1"
LICENSE_${PN}-gobject = "MPL-1.1 | LGPLv2.1"
LICENSE_${PN}-script-interpreter = "MPL-1.1 | LGPLv2.1"
LICENSE_${PN}-perf-utils = "GPLv3+"

LIC_FILES_CHKSUM = "file://COPYING;md5=e73e999e0c72b5ac9012424fa157ad77"

DEPENDS = "fontconfig glib-2.0 libpng pixman zlib"

SRC_URI = "http://cairographics.org/releases/cairo-${PV}.tar.xz \
           file://cairo-get_bitmap_surface-bsc1036789-CVE-2017-7475.diff \ 
           file://CVE-2018-19876.patch \
           file://CVE-2019-6461.patch \
           file://CVE-2019-6462.patch \
           file://CVE-2020-35492.patch \
          "

SRC_URI[md5sum] = "f19e0353828269c22bd72e271243a552"
SRC_URI[sha256sum] = "5e7b29b3f113ef870d1e3ecf8adf21f923396401604bda16d44be45e66052331"

inherit autotools pkgconfig upstream-version-is-even gtk-doc multilib_script

MULTILIB_SCRIPTS = "${PN}-perf-utils:${bindir}/cairo-trace"

X11DEPENDS = "virtual/libx11 libsm libxrender libxext"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'directfb', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11 xcb', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'x11 opengl', 'opengl', '', d)} \
                   trace"

PACKAGECONFIG[x11] = "--with-x=yes -enable-xlib,--with-x=no --disable-xlib,${X11DEPENDS}"
PACKAGECONFIG[xcb] = "--enable-xcb,--disable-xcb,libxcb"
PACKAGECONFIG[directfb] = "--enable-directfb=yes,,directfb"
PACKAGECONFIG[valgrind] = "--enable-valgrind=yes,--disable-valgrind,valgrind"
PACKAGECONFIG[egl] = "--enable-egl=yes,--disable-egl,virtual/egl"
PACKAGECONFIG[glesv2] = "--enable-glesv2,--disable-glesv2,virtual/libgles2"
PACKAGECONFIG[opengl] = "--enable-gl,--disable-gl,virtual/libgl"
PACKAGECONFIG[trace] = "--enable-trace,--disable-trace"

EXTRA_OECONF += " \
    ${@bb.utils.contains('TARGET_FPU', 'soft', '--disable-some-floating-point', '', d)} \
    --enable-tee \
"

# We don't depend on binutils so we need to disable this
export ac_cv_lib_bfd_bfd_openr="no"
# Ensure we don't depend on LZO
export ac_cv_lib_lzo2_lzo2a_decompress="no"

do_install_append () {
	rm -rf ${D}${bindir}/cairo-sphinx
	rm -rf ${D}${libdir}/cairo/cairo-fdr*
	rm -rf ${D}${libdir}/cairo/cairo-sphinx*
	rm -rf ${D}${libdir}/cairo/.debug/cairo-fdr*
	rm -rf ${D}${libdir}/cairo/.debug/cairo-sphinx*
	[ ! -d ${D}${bindir} ] ||
		rmdir -p --ignore-fail-on-non-empty ${D}${bindir}
	[ ! -d ${D}${libdir}/cairo ] ||
		rmdir -p --ignore-fail-on-non-empty ${D}${libdir}/cairo
}

PACKAGES =+ "cairo-gobject cairo-script-interpreter cairo-perf-utils"

SUMMARY_cairo-gobject = "The Cairo library GObject wrapper library"
DESCRIPTION_cairo-gobject = "A GObject wrapper library for the Cairo API."

SUMMARY_cairo-script-interpreter = "The Cairo library script interpreter"
DESCRIPTION_cairo-script-interpreter = "The Cairo script interpreter implements \
CairoScript.  CairoScript is used by tracing utilities to enable the ability \
to replay rendering."

DESCRIPTION_cairo-perf-utils = "The Cairo library performance utilities"

FILES_${PN} = "${libdir}/libcairo.so.*"
FILES_${PN}-gobject = "${libdir}/libcairo-gobject.so.*"
FILES_${PN}-script-interpreter = "${libdir}/libcairo-script-interpreter.so.*"
FILES_${PN}-perf-utils = "${bindir}/cairo-trace* ${libdir}/cairo/*.la ${libdir}/cairo/libcairo-trace.so"

BBCLASSEXTEND = "native nativesdk"

UPSTREAM_CHECK_REGEX = "cairo-(?P<pver>\d+(\.\d+)+).tar.xz"
