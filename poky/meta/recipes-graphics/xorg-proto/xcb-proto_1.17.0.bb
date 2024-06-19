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

SRC_URI = "https://xorg.freedesktop.org/archive/individual/proto/${BP}.tar.xz \
           file://0001-xcb-proto.pc.in-reinstate-libdir.patch \
           file://0001-Fix-install-conflict-when-enable-multilib.patch \
           "
SRC_URI[sha256sum] = "2c1bacd2110f4799f74de6ebb714b94cf6f80fb112316b1219480fd22562148c"

inherit autotools pkgconfig python3native

PACKAGES += "python-xcbgen"

FILES:${PN} = ""
FILES:${PN}-dev += "${datadir}/xcb/*.xml ${datadir}/xcb/*.xsd"
FILES:python-xcbgen = "${PYTHON_SITEPACKAGES_DIR}"

DEV_PKG_DEPENDENCY = ""
RRECOMMENDS:${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"

BBCLASSEXTEND = "native nativesdk"
