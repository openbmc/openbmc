SUMMARY = "Free Implementation of the Unicode Bidirectional Algorithm"
DESCRIPTION = "It provides utility functions to aid in the development \
of interactive editors and widgets that implement BiDi functionality. \
The BiDi algorithm is a prerequisite for supporting right-to-left scripts such \
as Hebrew, Arabic, Syriac, and Thaana. "
SECTION = "libs"
HOMEPAGE = "http://fribidi.org/"
BUGTRACKER = "https://github.com/fribidi/fribidi/issues"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=a916467b91076e631dd8edb7424769c7"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/v${PV}/${BP}.tar.xz \
           "
SRC_URI[sha256sum] = "0cd233f97fc8c67bb3ac27ce8440def5d3ffacf516765b91c2cc654498293495"

UPSTREAM_CHECK_URI = "https://github.com/${BPN}/${BPN}/releases"

inherit meson lib_package pkgconfig

CVE_PRODUCT = "gnu_fribidi fribidi"

BBCLASSEXTEND = "native nativesdk"
