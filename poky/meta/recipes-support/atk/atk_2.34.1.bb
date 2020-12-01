SUMMARY = "Accessibility toolkit for GNOME"
HOMEPAGE = "https://wiki.gnome.org/Accessibility"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/atk/-/issues"
SECTION = "x11/libs"

LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7 \
                    file://atk/atkutil.c;endline=18;md5=6fd31cd2fdc9b30f619ca8d819bc12d3 \
                    file://atk/atk.h;endline=18;md5=fcd7710187e0eae485e356c30d1b0c3b"

# Need gettext-native as Meson can't turn off i18n
DEPENDS = "gettext-native glib-2.0"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase gtk-doc gettext upstream-version-is-even gobject-introspection

SRC_URI[archive.md5sum] = "f60bbaf8bdd08b93d98736b54b2fc8e9"
SRC_URI[archive.sha256sum] = "d4f0e3b3d21265fcf2bc371e117da51c42ede1a71f6db1c834e6976bb20997cb"

BBCLASSEXTEND = "native nativesdk"
