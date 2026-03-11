SUMMARY = "Evolution database backend server"
HOMEPAGE = "http://www.gnome.org/projects/evolution/"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "LGPL-2.0-only & LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=6a6e689d19255cf0557f3fe7d7068212 \
                    file://src/camel/camel.h;endline=24;md5=4150eec6c74e359401d8ed7f266984b9 \
                    file://src/libedataserver/e-data-server-util.h;endline=20;md5=fa7fca32c1bf07f87a52094b414fe7a8 \
                    "

GNOMEBASEBUILDCLASS = "cmake"
inherit gnomebase upstream-version-is-even

SRC_URI[archive.sha256sum] = "df4ec29950f29a76eac6fbe0f814c48d2cef7d3fdb905002a4a883dd761ce93c"
PV = "3.56.2"
