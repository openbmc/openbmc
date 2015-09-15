SUMMARY = "Cascading Style Sheet (CSS) parsing and manipulation toolkit"
HOMEPAGE = "http://www.gnome.org/"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "LGPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=55ca817ccb7d5b5b66355690e9abc605 \
                    file://src/cr-rgb.c;endline=22;md5=31d5f0944d556c8589d04ea6055fcc66 \
                    file://tests/cr-test-utils.c;endline=21;md5=2382c27934cae1d3792fcb17a6142c4e"

SECTION = "x11/utils"
DEPENDS = "glib-2.0 libxml2 zlib"
BBCLASSEXTEND = "native"
EXTRA_OECONF += "--enable-Bsymbolic=auto"
PR = "r2"

BINCONFIG = "${bindir}/croco-0.6-config"

inherit autotools pkgconfig gnomebase gtk-doc binconfig-disabled

SRC_URI[archive.md5sum] = "767e73c4174f75b99695d4530fd9bb80"
SRC_URI[archive.sha256sum] = "ea6e1b858c55219cefd7109756bff5bc1a774ba7a55f7d3ccd734d6b871b8570"
