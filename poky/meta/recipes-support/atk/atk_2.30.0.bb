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

GTKDOC_ENABLE_FLAG = "-Ddocs=true"
GTKDOC_DISABLE_FLAG = "-Ddocs=false"

GI_ENABLE_FLAG = "-Dintrospection=true"
GI_DISABLE_FLAG = "-Dintrospection=false"

EXTRA_OEMESON_append_class-nativesdk = " ${GI_DISABLE_FLAG}"

EXTRA_OEMESON_append_class-target = " ${@bb.utils.contains('GI_DATA_ENABLED', 'True', '${GI_ENABLE_FLAG}', \
                                                                                       '${GI_DISABLE_FLAG}', d)} "

EXTRA_OEMESON_append_class-target = " ${@bb.utils.contains('GTKDOC_ENABLED', 'True', '${GTKDOC_ENABLE_FLAG}', \
                                                                                     '${GTKDOC_DISABLE_FLAG}', d)} "

SRC_URI_append = " \
                   file://0001-meson.build-enable-introspection-for-cross-compile.patch \
                   file://0001-Switch-from-filename-to-basename.patch \
                   "
SRC_URI[archive.md5sum] = "769c85005d392ad17ffbc063f2d26454"
SRC_URI[archive.sha256sum] = "dd4d90d4217f2a0c1fee708a555596c2c19d26fef0952e1ead1938ab632c027b"

BBCLASSEXTEND = "native nativesdk"

