SUMMARY = "GObject-based XIM protocol library"
DESCRIPTION = "libgxim is a X Input Method protocol library that is implemented by GObject.\
this library helps you to implement XIM servers or client applications to\
communicate through XIM protocol without using Xlib API directly, particularly\
if your application uses GObject-based main loop.\
\
This package contains the shared library."

HOMEPAGE = "http://code.google.com/p/libgxim/"
SECTION = "System Environment/Libraries"

SRC_URI = "https://bitbucket.org/tagoh/libgxim/downloads/${BPN}-${PV}.tar.bz2 \
           file://multi-line-ACLOCAL_AMFLAGS-isnot-supported-by-autoreconf.patch"

SRC_URI[md5sum] = "4bb1fa63d00eb224439d413591c29a6a"
SRC_URI[sha256sum] = "75e20d1744139644f9951b78ea3910b162d3380302315cb4b3d0640f23694c79"

LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "\
file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

EXTRA_OECONF = " --disable-static --disable-rebuilds"
DEPENDS += "gtk+ glib-2.0 ruby-native"

inherit autotools pkgconfig gettext
