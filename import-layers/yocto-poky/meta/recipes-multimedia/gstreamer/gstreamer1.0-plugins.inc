SUMMARY = "Plugins for the GStreamer multimedia framework 1.x"
HOMEPAGE = "http://gstreamer.freedesktop.org/"
BUGTRACKER = "https://bugzilla.gnome.org/enter_bug.cgi?product=Gstreamer"
SECTION = "multimedia"

DEPENDS = "gstreamer1.0 glib-2.0-native"

inherit autotools pkgconfig upstream-version-is-even gobject-introspection

acpaths = "-I ${S}/common/m4 -I ${S}/m4"

LIBV = "1.0"
require gst-plugins-package.inc

# Orc enables runtime JIT compilation of data processing routines from Orc
# bytecode to SIMD instructions for various architectures (currently SSE, MMX,
# MIPS, Altivec and NEON are supported).

GSTREAMER_ORC ?= "orc"

PACKAGECONFIG[debug] = "--enable-debug,--disable-debug"
PACKAGECONFIG[orc] = "--enable-orc,--disable-orc,orc orc-native"
PACKAGECONFIG[valgrind] = "--enable-valgrind,--disable-valgrind,valgrind"

export ORCC = "${STAGING_DIR_NATIVE}${bindir}/orcc"

EXTRA_OECONF = " \
    --disable-examples \
"

SRC_URI_append = " file://0001-introspection.m4-prefix-pkgconfig-paths-with-PKG_CON.patch"

delete_pkg_m4_file() {
	# This m4 file is out of date and is missing PKG_CONFIG_SYSROOT_PATH tweaks which we need for introspection
	rm "${S}/common/m4/pkg.m4" || true
}

do_configure[prefuncs] += " delete_pkg_m4_file"

PACKAGES_DYNAMIC = "^${PN}-.*"
