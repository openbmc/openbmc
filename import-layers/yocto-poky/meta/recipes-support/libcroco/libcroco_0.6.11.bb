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

BINCONFIG = "${bindir}/croco-0.6-config"

inherit autotools pkgconfig gnomebase gtk-doc binconfig-disabled

SRC_URI[archive.md5sum] = "dabc1911dfbfa85f8e6859ca47863168"
SRC_URI[archive.sha256sum] = "132b528a948586b0dfa05d7e9e059901bca5a3be675b6071a90a90b81ae5a056"
