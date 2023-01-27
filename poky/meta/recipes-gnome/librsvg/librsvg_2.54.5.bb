SUMMARY = "Library for rendering SVG files"
DESCRIPTION = "A small library to render Scalable Vector Graphics (SVG), \
associated with the GNOME Project. It renders SVG files to Cairo surfaces. \
Cairo is the 2D, antialiased drawing library that GNOME uses to draw things to \
the screen or to generate output for printing."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/librsvg"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/librsvg/issues"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
                   "

SECTION = "x11/utils"
DEPENDS = "cairo gdk-pixbuf glib-2.0 libxml2 pango python3-docutils-native"
BBCLASSEXTEND = "native nativesdk"

inherit cargo_common gnomebase pixbufcache upstream-version-is-even gobject-introspection rust vala gi-docgen

SRC_URI += "file://0001-Makefile.am-pass-rust-target-to-cargo-also-when-not-.patch \
           file://0001-system-deps-src-lib.rs-do-not-probe-into-harcoded-li.patch \
           "

SRC_URI[archive.sha256sum] = "4f03190f45324d1fa1f52a79dfcded1f64eaf49b3ae2f88eedab0c07617cae6e"

# librsvg is still autotools-based, but is calling cargo from its automake-driven makefiles
# so we cannot use cargo class directly, but still need bits and pieces from it 
# for cargo to be happy
BASEDEPENDS:append = " cargo-native"

export RUST_BACKTRACE = "full"
export RUSTFLAGS

export RUST_TARGET = "${RUST_HOST_SYS}"

RUSTFLAGS:append:mips = " --cfg crossbeam_no_atomic_64"
RUSTFLAGS:append:mipsel = " --cfg crossbeam_no_atomic_64"
RUSTFLAGS:append:powerpc = " --cfg crossbeam_no_atomic_64"
RUSTFLAGS:append:riscv32 = " --cfg crossbeam_no_atomic_64"

CARGO_DISABLE_BITBAKE_VENDORING = "1"
do_configure[postfuncs] += "cargo_common_do_configure"

inherit rust-target-config

# rust-cross writes the target linker binary into target json definition without any flags.
# This breaks here because the linker isn't going to work without at least knowing where
# the sysroot is. So copy the json to workdir, and patch in the path to wrapper from rust class
# which supplies the needed flags.
do_compile:prepend() {
    sed -ie 's,"linker": ".*","linker": "${RUST_TARGET_CC}",g' ${RUST_TARGETS_DIR}/${RUST_HOST_SYS}.json
}

# Issue only on windows
CVE_CHECK_IGNORE += "CVE-2018-1000041"

CACHED_CONFIGUREVARS = "ac_cv_path_GDK_PIXBUF_QUERYLOADERS=${STAGING_LIBDIR_NATIVE}/gdk-pixbuf-2.0/gdk-pixbuf-query-loaders"

PACKAGECONFIG ??= "gdkpixbuf"
PACKAGECONFIG:append:class-target = " ${@bb.utils.contains('GI_DATA_ENABLED', 'True', 'vala', '', d)}"
# The gdk-pixbuf loader
PACKAGECONFIG[gdkpixbuf] = "--enable-pixbuf-loader,--disable-pixbuf-loader,gdk-pixbuf-native"
PACKAGECONFIG[vala] = "--enable-vala,--disable-vala"

do_install:append() {
	# Loadable modules don't need .a or .la on Linux
	rm -f ${D}${libdir}/gdk-pixbuf-2.0/*/loaders/*.a ${D}${libdir}/gdk-pixbuf-2.0/*/loaders/*.la
}

PACKAGES =+ "librsvg-gtk rsvg"
FILES:rsvg = "${bindir}/rsvg* \
	      ${datadir}/pixmaps/svg-viewer.svg \
	      ${datadir}/themes"
FILES:librsvg-gtk = "${libdir}/gdk-pixbuf-2.0/*/*/*.so \
                     ${datadir}/thumbnailers/librsvg.thumbnailer"
RRECOMMENDS:librsvg-gtk = "gdk-pixbuf-bin"

PIXBUF_PACKAGES = "librsvg-gtk"
