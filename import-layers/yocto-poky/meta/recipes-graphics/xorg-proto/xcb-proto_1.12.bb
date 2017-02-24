SUMMARY = "XCB: The X protocol C binding headers"
DESCRIPTION = "Function prototypes for the X protocol C-language Binding \
(XCB).  XCB is a replacement for Xlib featuring a small footprint, \
latency hiding, direct access to the protocol, improved threading \
support, and extensibility."
HOMEPAGE = "http://xcb.freedesktop.org"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=XCB"

SECTION = "x11/libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d763b081cb10c223435b01e00dc0aba7 \
                    file://src/dri2.xml;beginline=2;endline=28;md5=f8763b13ff432e8597e0d610cf598e65"

SRC_URI = "http://xcb.freedesktop.org/dist/xcb-proto-${PV}.tar.bz2 \
           file://no-python-native.patch \
           file://0001-Make-whitespace-use-consistent.patch \
           file://0002-print-is-a-function-and-needs-parentheses.patch \
           "
SRC_URI[md5sum] = "14e60919f859560f28426a685a555962"
SRC_URI[sha256sum] = "5922aba4c664ab7899a29d92ea91a87aa4c1fc7eb5ee550325c3216c480a4906"

inherit autotools pkgconfig

PACKAGES += "python-xcbgen"

FILES_${PN} = ""
FILES_${PN}-dev += "${datadir}/xcb/*.xml ${datadir}/xcb/*.xsd"
FILES_python-xcbgen = "${libdir}/xcb-proto"

RDEPENDS_${PN}-dev = ""
RRECOMMENDS_${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"

BBCLASSEXTEND = "native nativesdk"

do_install_append() {
    # Makefile's do_install creates .pyc files for python3, now also create
    # them for python2 so that they will be recorded by manifest, and can be
    # cleaned correctly.
    python -m py_compile ${D}${libdir}/xcb-proto/xcbgen/*.py
}
