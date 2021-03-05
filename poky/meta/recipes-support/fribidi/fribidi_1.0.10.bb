SUMMARY = "Free Implementation of the Unicode Bidirectional Algorithm"
DESCRIPTION = "It provides utility functions to aid in the development \
of interactive editors and widgets that implement BiDi functionality. \
The BiDi algorithm is a prerequisite for supporting right-to-left scripts such \
as Hebrew, Arabic, Syriac, and Thaana. "
SECTION = "libs"
HOMEPAGE = "http://fribidi.org/"
BUGTRACKER = "https://github.com/fribidi/fribidi/issues"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=a916467b91076e631dd8edb7424769c7"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/v${PV}/${BP}.tar.xz \
           "
SRC_URI[md5sum] = "97c87da9930e8e70fbfc8e2bcd031554"
SRC_URI[sha256sum] = "7f1c687c7831499bcacae5e8675945a39bacbad16ecaa945e9454a32df653c01"

UPSTREAM_CHECK_URI = "https://github.com/${BPN}/${BPN}/releases"

inherit meson lib_package pkgconfig

CVE_PRODUCT = "gnu_fribidi fribidi"

BBCLASSEXTEND = "native nativesdk"
