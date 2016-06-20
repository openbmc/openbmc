SUMMARY = "XCB: The X protocol C binding headers"
DESCRIPTION = "Function prototypes for the X protocol C-language Binding \
(XCB).  XCB is a replacement for Xlib featuring a small footprint, \
latency hiding, direct access to the protocol, improved threading \
support, and extensibility."
HOMEPAGE = "http://xcb.freedesktop.org"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=XCB"

SECTION = "x11/libs"
LICENSE = "MIT-X"

SRC_URI = "http://xcb.freedesktop.org/dist/xcb-proto-${PV}.tar.bz2 \
           file://no-python-native.patch"

inherit autotools pkgconfig

PACKAGES += "python-xcbgen"

FILES_${PN} = ""
FILES_${PN}-dev += "${datadir}/xcb/*.xml ${datadir}/xcb/*.xsd"
FILES_python-xcbgen = "${libdir}/xcb-proto"

RDEPENDS_${PN}-dev = ""
RRECOMMENDS_${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"

BBCLASSEXTEND = "native nativesdk"
