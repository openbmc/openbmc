SUMMARY = "FLTK is a cross-platform C++ GUI toolkit"
HOMEPAGE = "https://www.fltk.org"
SECTION = "libs"
LICENSE = "LGPL-2.0-only & FLTK"
LIC_FILES_CHKSUM = "file://COPYING;md5=f6b26344a24a941a01a5b0826e80b5ca"

SRC_URI = " \
    https://github.com/fltk/fltk/releases/download/release-${PV}/${BP}-source.tar.bz2 \
    file://disable_test.patch \
    file://0001-Fl_Preferences.cxx-do-not-use-dlopen-in-case-glibc-s.patch \
    file://0002-always-build-fluid-and-export-pointers.patch \
"

PV = "1.3.11"
SRC_URI[sha256sum] = "ca2e144e5f89173cd094cc273940d56230b1bf613083a0792e6406dc191cd99f"

inherit cmake pkgconfig

TARGET_CC_ARCH += "${LDFLAGS} -DXFT_MAJOR=2"
