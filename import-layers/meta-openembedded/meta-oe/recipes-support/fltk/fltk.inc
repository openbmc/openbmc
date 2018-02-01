SUMMARY = "FLTK is a cross-platform C++ GUI toolkit"
HOMEPAGE = "http://www.fltk.org"
SECTION = "libs"
LICENSE = "LGPLv2 & FLTK"
LIC_FILES_CHKSUM = "file://COPYING;md5=f6b26344a24a941a01a5b0826e80b5ca"

SRC_URI = " \
    http://fltk.org/pub/fltk/1.3.4/${BP}-source.tar.gz \
    file://disable_test.patch \
    file://fltk-no-freetype-config.patch \
    file://0001-Fl_Preferences.cxx-do-not-use-dlopen-in-case-glibc-s.patch \
    file://0002-always-build-fluid-and-export-pointers.patch \
"

PV = "1.3.4-1"
SRC_URI[md5sum] = "d7fcd27ab928648e1a1366dd2e273970"
SRC_URI[sha256sum] = "7fb2c8882433ce694e6900c94fda505e8f4ed3fa9c7e597007098a33b85c53f4"

inherit cmake pkgconfig

TARGET_CC_ARCH += "${LDFLAGS} -DXFT_MAJOR=2"
