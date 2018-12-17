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

PV = "1.3.4-2"
SRC_URI[md5sum] = "b8e291343357e49dd81a22408744e400"
SRC_URI[sha256sum] = "25d349c18c99508737d48f225a2eb26a43338f9247551cab72a317fa42cda910"

inherit cmake pkgconfig

TARGET_CC_ARCH += "${LDFLAGS} -DXFT_MAJOR=2"
