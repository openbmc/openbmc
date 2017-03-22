SUMMARY = "Accessibility toolkit for GNOME"
HOMEPAGE = "http://live.gnome.org/GAP/"
BUGTRACKER = "https://bugzilla.gnome.org/"
SECTION = "x11/libs"

LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7 \
                    file://atk/atkutil.c;endline=18;md5=6fd31cd2fdc9b30f619ca8d819bc12d3 \
                    file://atk/atk.h;endline=18;md5=fcd7710187e0eae485e356c30d1b0c3b"

DEPENDS = "glib-2.0"

inherit gnomebase gtk-doc gettext upstream-version-is-even gobject-introspection

SRC_URI[archive.md5sum] = "5187b0972f4d3905f285540b31395e20"
SRC_URI[archive.sha256sum] = "493a50f6c4a025f588d380a551ec277e070b28a82e63ef8e3c06b3ee7c1238f0"

BBCLASSEXTEND = "native"

EXTRA_OECONF = "--disable-glibtest \
               "
