SUMMARY = "Accessibility toolkit for GNOME"
DESCRIPTION = "Provides application programming interfaces (APIs) for implementing accessibility support in software."
HOMEPAGE = "https://wiki.gnome.org/Accessibility"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/atk/-/issues"
SECTION = "x11/libs"

LICENSE = "GPL-2.0-or-later & LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7 \
                    file://atk/atkutil.c;endline=18;md5=6fd31cd2fdc9b30f619ca8d819bc12d3 \
                    file://atk/atk.h;endline=18;md5=fcd7710187e0eae485e356c30d1b0c3b"

# Need gettext-native as Meson can't turn off i18n
DEPENDS = "gettext-native glib-2.0"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase gtk-doc gettext upstream-version-is-even gobject-introspection

SRC_URI[archive.sha256sum] = "ac4de2a4ef4bd5665052952fe169657e65e895c5057dffb3c2a810f6191a0c36"

BBCLASSEXTEND = "native nativesdk"
