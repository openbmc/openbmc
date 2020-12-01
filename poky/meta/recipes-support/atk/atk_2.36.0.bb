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

SRC_URI[archive.md5sum] = "01aa5ec5138f5f8c9b3a4e3196ed2900"
SRC_URI[archive.sha256sum] = "fb76247e369402be23f1f5c65d38a9639c1164d934e40f6a9cf3c9e96b652788"

BBCLASSEXTEND = "native nativesdk"
