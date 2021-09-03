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

PV = "1.3.7"
SRC_URI[sha256sum] = "5ff1e90766ff4216e78e9a19a9bf527e6258f04accccd4429e9bb829b01d690d"

inherit cmake pkgconfig

TARGET_CC_ARCH += "${LDFLAGS} -DXFT_MAJOR=2"
