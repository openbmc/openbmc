SUMMARY = "Evolution database backend server"
HOMEPAGE = "http://www.gnome.org/projects/evolution/"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "LGPL-2.0-only AND LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=6a6e689d19255cf0557f3fe7d7068212 \
                    file://src/camel/camel.h;endline=5;md5=fd00ddcc6fd84a9e7c8a3b1f760617da \
                    file://src/libedataserver/e-data-server-util.h;endline=7;md5=48ebc221795bd4ff17dec7035065e865 \
                    "

GNOMEBASEBUILDCLASS = "cmake"
inherit gnomebase upstream-version-is-even

SRC_URI[archive.sha256sum] = "78be88e9a511f5afe7ff2e363eade84b622dd0282647fc576edb2d4ff2962cba"
PV = "3.62.0"
