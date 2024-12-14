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
DEPENDS = "cairo gdk-pixbuf glib-2.0 libxml2 pango python3-docutils-native cargo-c-native"
BBCLASSEXTEND = "native nativesdk"

inherit cargo_common gnomebase pixbufcache gobject-introspection rust vala gi-docgen cargo-update-recipe-crates
GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'
GIDOCGEN_MESON_OPTION = 'docs'
GIDOCGEN_MESON_ENABLE_FLAG = 'enabled'
GIDOCGEN_MESON_DISABLE_FLAG = 'disabled'

require ${BPN}-crates.inc

SRC_URI += "file://0001-meson.build-do-not-force-disable-introspection-and-v.patch \
            file://0001-gdk-pixbuf-loader-meson.build-do-not-look-for-gdk-pi.patch"
SRC_URI[archive.sha256sum] = "ecd293fb0cc338c170171bbc7bcfbea6725d041c95f31385dc935409933e4597"

UPSTREAM_CHECK_REGEX = "librsvg-(?P<pver>\d+\.\d+\.(?!9\d+)\d+)"

# librsvg is still autotools-based, but is calling cargo from its automake-driven makefiles
# so we cannot use cargo class directly, but still need bits and pieces from it 
# for cargo to be happy
BASEDEPENDS:append = " cargo-native"

export EXTRA_NATIVE_PKGCONFIG_PATH = "${B}/meson-uninstalled:"
export RUST_BACKTRACE = "full"
export RUSTFLAGS

RUSTFLAGS:append:mips = " --cfg crossbeam_no_atomic_64"
RUSTFLAGS:append:mipsel = " --cfg crossbeam_no_atomic_64"
RUSTFLAGS:append:powerpc = " --cfg crossbeam_no_atomic_64"

do_configure[postfuncs] += "cargo_common_do_configure"

inherit rust-target-config

# rust-cross writes the target linker binary into target json definition without any flags.
# This breaks here because the linker isn't going to work without at least knowing where
# the sysroot is. So copy the json to workdir, and patch in the path to wrapper from rust class
# which supplies the needed flags.
do_compile:prepend() {
    sed -ie 's,"linker": ".*","linker": "${RUST_TARGET_CC}",g' ${RUST_TARGETS_DIR}/${RUST_HOST_SYS}.json
}

CVE_STATUS[CVE-2018-1000041] = "not-applicable-platform: Issue only applies on Windows"

EXTRA_OEMESON = "-Dtriplet=${RUST_HOST_SYS}"
LDFLAGS += " -L${B}/rsvg"
# needed on ubuntu 20.04/debian 11 to avoid 'undefined reference to `dlsym'' errors
BUILD_LDFLAGS += " -ldl"

PACKAGECONFIG ??= "gdkpixbuf"
PACKAGECONFIG:append:class-target = " ${@bb.utils.contains('GI_DATA_ENABLED', 'True', 'vala', '', d)}"
# The gdk-pixbuf loader
PACKAGECONFIG[gdkpixbuf] = "-Dpixbuf-loader=enabled,-Dpixbuf-loader=disabled,gdk-pixbuf-native"
PACKAGECONFIG[vala] = "-Dvala=enabled,-Dvala=disabled"

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

SYSROOT_PREPROCESS_FUNCS:append:class-target = " pkgconfig_sysroot_preprocess"

# installed .pc file contains
# prefix=${pcfiledir}/../..
# which is expanded to an absolute path, and thus doesn't work when additionally prefixed with a sysroot directory
pkgconfig_sysroot_preprocess() {
        sed -i -e "s:^prefix=.*:prefix=${prefix}:g" ${SYSROOT_DESTDIR}${libdir}/pkgconfig/librsvg-2.0.pc
}
