SUMMARY = "Library for embedding a Clutter canvas in a GTK+ application"
HOMEPAGE = "http://www.clutter-project.org/"
LICENSE = "LGPLv2+"

inherit clutter distro_features_check upstream-version-is-even gobject-introspection
# depends on clutter-1.0 which depends on cogl-1.0
REQUIRED_DISTRO_FEATURES ?= "opengl"
# depends on gtk+3
ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

DEPENDS = "clutter-1.0 gtk+3"
PACKAGES  =+ "${PN}-examples"
AUTOTOOLS_AUXDIR = "${S}/build"


