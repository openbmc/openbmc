SUMMARY = "Library for converting characters to X key-presses"
DESCRIPTION = "libfakekey is a simple library for converting UTF-8 characters into 'fake' X \
key-presses."
HOMEPAGE = "http://matchbox-project.org/"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"

LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://src/libfakekey.c;endline=30;md5=602b5ccd48f64407510867f3373b448c"

DEPENDS = "libxtst"
SECTION = "x11/wm"

SRCREV = "e327ff049b8503af2dadffa84370a0860b9fb682"
PV = "0.0+git${SRCPV}"

SRC_URI = "git://git.yoctoproject.org/${BPN}"

S = "${WORKDIR}/git"

inherit autotools pkgconfig gettext distro_features_check

# The libxtst requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"
