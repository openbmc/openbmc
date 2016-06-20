SUMMARY = "Accessibility toolkit for GNOME"
HOMEPAGE = "http://live.gnome.org/GAP/"
BUGTRACKER = "https://bugzilla.gnome.org/"
SECTION = "x11/libs"

LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7 \
                    file://atk/atkutil.c;endline=18;md5=6fd31cd2fdc9b30f619ca8d819bc12d3 \
                    file://atk/atk.h;endline=18;md5=fcd7710187e0eae485e356c30d1b0c3b"

DEPENDS = "glib-2.0"

inherit gnomebase gtk-doc upstream-version-is-even gobject-introspection

SRC_URI[archive.md5sum] = "fd3678f35004b4c92e3da39356996054"
SRC_URI[archive.sha256sum] = "ce6c48d77bf951083029d5a396dd552d836fff3c1715d3a7022e917e46d0c92b"

BBCLASSEXTEND = "native"

EXTRA_OECONF = "--disable-glibtest \
               "
