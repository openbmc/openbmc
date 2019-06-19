SUMMARY = "Accessibility toolkit for GNOME"
HOMEPAGE = "http://live.gnome.org/GAP/"
BUGTRACKER = "https://bugzilla.gnome.org/"
SECTION = "x11/libs"

LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7 \
                    file://atk/atkutil.c;endline=18;md5=6fd31cd2fdc9b30f619ca8d819bc12d3 \
                    file://atk/atk.h;endline=18;md5=fcd7710187e0eae485e356c30d1b0c3b"

# Need gettext-native as Meson can't turn off i18n
DEPENDS = "gettext-native glib-2.0"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase gtk-doc gettext upstream-version-is-even gobject-introspection

SRC_URI += " file://0001-meson.build-enable-introspection-for-cross-compile.patch"
SRC_URI[archive.md5sum] = "c10b0b2af3c199e42caa6275b845c49d"
SRC_URI[archive.sha256sum] = "cb41feda7fe4ef0daa024471438ea0219592baf7c291347e5a858bb64e4091cc"

BBCLASSEXTEND = "native nativesdk"

