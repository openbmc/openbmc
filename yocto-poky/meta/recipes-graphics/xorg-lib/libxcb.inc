SUMMARY = "XCB: The X protocol C binding library"
DESCRIPTION = "The X protocol C-language Binding (XCB) is a replacement \
for Xlib featuring a small footprint, latency hiding, direct access to \
the protocol, improved threading support, and extensibility."
HOMEPAGE = "http://xcb.freedesktop.org"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=XCB"

BBCLASSEXTEND = "native nativesdk"

SECTION = "x11/libs"
LICENSE = "MIT-X"

DEPENDS = "xcb-proto xproto libxau xcb-proto-native libpthread-stubs"

SRC_URI = "http://xcb.freedesktop.org/dist/libxcb-${PV}.tar.bz2 \
           file://xcbincludedir.patch \
           file://disable-check.patch"

PACKAGES_DYNAMIC = "^libxcb-.*"

FILES_${PN} = "${libdir}/libxcb.so.*"

inherit autotools pkgconfig pythonnative distro_features_check

# The libxau and others requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"

python populate_packages_prepend () {
    do_split_packages(d, '${libdir}', '^libxcb-(.*)\.so\..*$', 'libxcb-%s', 'XCB library module for %s', allow_links=True)
}
