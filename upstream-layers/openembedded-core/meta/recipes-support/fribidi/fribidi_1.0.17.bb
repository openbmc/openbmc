SUMMARY = "Free Implementation of the Unicode Bidirectional Algorithm"
DESCRIPTION = "It provides utility functions to aid in the development \
of interactive editors and widgets that implement BiDi functionality. \
The BiDi algorithm is a prerequisite for supporting right-to-left scripts such \
as Hebrew, Arabic, Syriac, and Thaana. "
SECTION = "libs"
HOMEPAGE = "http://fribidi.org/"
BUGTRACKER = "https://github.com/fribidi/fribidi/issues"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4bf661c1e3793e55c8d1051bc5e0ae21"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/${BP}.tar.xz \
           "
SRC_URI[sha256sum] = "6949dcde27d41cebad1fd741fcafc36d55a1020d2d872d4a6eb3914caabbada2"

inherit meson lib_package pkgconfig github-releases

CVE_PRODUCT = "gnu_fribidi fribidi"

BBCLASSEXTEND = "native nativesdk"
