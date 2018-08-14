SUMMARY = "Library for parsing command line options"
HOMEPAGE = "http://rpm5.org/"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=cb0613c30af2a8249b8dcc67d3edb06d"
PR = "r3"

DEPENDS = "virtual/libiconv"

SRC_URI = "http://rpm5.org/files/popt/popt-${PV}.tar.gz \
           file://pkgconfig_fix.patch \
           file://popt_fix_for_automake-1.12.patch \
           file://disable_tests.patch \
          "

SRC_URI[md5sum] = "3743beefa3dd6247a73f8f7a32c14c33"
SRC_URI[sha256sum] = "e728ed296fe9f069a0e005003c3d6b2dde3d9cad453422a10d6558616d304cc8"

inherit autotools gettext

BBCLASSEXTEND = "native nativesdk"
