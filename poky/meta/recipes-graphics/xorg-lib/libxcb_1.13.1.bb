SUMMARY = "XCB: The X protocol C binding library"
DESCRIPTION = "The X protocol C-language Binding (XCB) is a replacement \
for Xlib featuring a small footprint, latency hiding, direct access to \
the protocol, improved threading support, and extensibility."
HOMEPAGE = "http://xcb.freedesktop.org"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=XCB"
SECTION = "x11/libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d763b081cb10c223435b01e00dc0aba7"

SRC_URI = "http://xcb.freedesktop.org/dist/libxcb-${PV}.tar.bz2 \
           file://xcbincludedir.patch \
           file://disable-check.patch \
          "

SRC_URI[md5sum] = "f33cdfc67346f7217a9326c0d8679975"
SRC_URI[sha256sum] = "a89fb7af7a11f43d2ce84a844a4b38df688c092bf4b67683aef179cdf2a647c4"

BBCLASSEXTEND = "native nativesdk"

DEPENDS = "xcb-proto xorgproto libxau libpthread-stubs libxdmcp"

PACKAGES_DYNAMIC = "^libxcb-.*"

FILES_${PN} = "${libdir}/libxcb.so.*"

inherit autotools pkgconfig features_check

# The libxau and others requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"

export PYTHON = "python3"

python populate_packages_prepend () {
    do_split_packages(d, '${libdir}', r'^libxcb-(.*)\.so\..*$', 'libxcb-%s', 'XCB library module for %s', allow_links=True)
}
