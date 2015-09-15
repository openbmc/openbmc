SUMMARY = "Library for embedding a Clutter canvas in a GTK+ application"
HOMEPAGE = "http://www.clutter-project.org/"
LICENSE = "LGPLv2+"

inherit clutter

DEPENDS = "clutter-1.0 gtk+3"
PACKAGES  =+ "${PN}-examples"
AUTOTOOLS_AUXDIR = "${S}/build"

EXTRA_OECONF += "--disable-introspection"

