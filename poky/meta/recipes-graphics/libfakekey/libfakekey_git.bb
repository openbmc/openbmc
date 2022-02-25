SUMMARY = "Library for converting characters to X key-presses"
DESCRIPTION = "libfakekey is a simple library for converting UTF-8 characters into 'fake' X \
key-presses."
HOMEPAGE = "http://matchbox-project.org/"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"

LICENSE = "LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://src/libfakekey.c;endline=30;md5=602b5ccd48f64407510867f3373b448c"

DEPENDS = "libxtst"
SECTION = "x11/wm"

SRCREV = "7ad885912efb2131e80914e964d5e635b0d07b40"
PV = "0.3+git${SRCPV}"

SRC_URI = "git://git.yoctoproject.org/${BPN};branch=master"

S = "${WORKDIR}/git"

inherit autotools pkgconfig gettext features_check

# The libxtst requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"
