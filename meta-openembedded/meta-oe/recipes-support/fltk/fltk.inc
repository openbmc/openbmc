SUMMARY = "FLTK is a cross-platform C++ GUI toolkit"
HOMEPAGE = "http://www.fltk.org"
SECTION = "libs"
LICENSE = "LGPLv2 & FLTK"
LIC_FILES_CHKSUM = "file://COPYING;md5=f6b26344a24a941a01a5b0826e80b5ca"

SRC_URI = " \
    http://fltk.org/pub/fltk/${PV}/${BP}-source.tar.bz2 \
    file://disable_test.patch \
    file://0001-Fl_Preferences.cxx-do-not-use-dlopen-in-case-glibc-s.patch \
    file://0002-always-build-fluid-and-export-pointers.patch \
"

PV = "1.3.5"
SRC_URI[md5sum] = "0de2b45a1896be2b4a8cafae89b84248"
SRC_URI[sha256sum] = "2933c72400f9e7c0f4c3a81a1ce98bc9582b2a3edc44b8597ccd26e240e32c3c"

inherit cmake pkgconfig

TARGET_CC_ARCH += "${LDFLAGS} -DXFT_MAJOR=2"
