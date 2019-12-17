SUMMARY = "XCB: The X protocol C binding utilities"
DESCRIPTION = "The xcb-util module provides a number of libraries which \
sit on top of libxcb, the core X protocol library, and some of the \
extension libraries. These experimental libraries provide convenience \
functions and interfaces which make the raw X protocol more usable. Some \
of the libraries also provide client-side code which is not strictly \
part of the X protocol but which have traditionally been provided by \
Xlib."
HOMEPAGE = "http://xcb.freedesktop.org/XcbUtil/"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=XCB"

LICENSE = "MIT"

SECTION = "x11/libs"

DEPENDS = "libxcb"
DEPENDS += "gperf-native"

SRC_URI = "http://xcb.freedesktop.org/dist/${BPN}-${PV}.tar.bz2"

inherit autotools pkgconfig features_check

REQUIRED_DISTRO_FEATURES = "x11"
