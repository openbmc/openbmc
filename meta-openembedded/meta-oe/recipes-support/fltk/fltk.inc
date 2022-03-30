SUMMARY = "FLTK is a cross-platform C++ GUI toolkit"
HOMEPAGE = "http://www.fltk.org"
SECTION = "libs"
LICENSE = "LGPL-2.0-only & FLTK"
LIC_FILES_CHKSUM = "file://COPYING;md5=f6b26344a24a941a01a5b0826e80b5ca"

SRC_URI = " \
    http://fltk.org/pub/fltk/${PV}/${BP}-source.tar.bz2 \
    file://disable_test.patch \
    file://0001-Fl_Preferences.cxx-do-not-use-dlopen-in-case-glibc-s.patch \
    file://0002-always-build-fluid-and-export-pointers.patch \
"

PV = "1.3.8"
SRC_URI[sha256sum] = "1d9832409fc08c908c64ab40ad9fd8b0e8af5d203651eb9690b810f41e2f412d"

inherit cmake pkgconfig

TARGET_CC_ARCH += "${LDFLAGS} -DXFT_MAJOR=2"
