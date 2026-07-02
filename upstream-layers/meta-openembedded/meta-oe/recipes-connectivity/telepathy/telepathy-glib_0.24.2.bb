SUMMARY = "Telepathy Framework glib-base helper library"
DESCRIPTION = "Telepathy Framework: GLib-based helper library for connection managers"
HOMEPAGE = "http://telepathy.freedesktop.org/wiki/"
DEPENDS = "glib-2.0 dbus hostpython-runtime-native dbus-native dbus-glib libxslt-native"
LICENSE = "LGPL-2.1-or-later"

SRC_URI = "http://telepathy.freedesktop.org/releases/telepathy-glib/${BP}.tar.gz"
SRC_URI[sha256sum] = "b0a374d771cdd081125f38c3abd079657642301c71a543d555e2bf21919273f0"

LIC_FILES_CHKSUM = "file://COPYING;md5=e413d83db6ee8f2c8e6055719096a48e"

inherit autotools pkgconfig gettext gobject-introspection vala gtk-doc

# GCC 14+ promotes -Wincompatible-pointer-types to an error. This 0.24.2
# release predates the stricter C rules (e.g. g_ptr_array_new_full callback
# casts in protocol.c).
CFLAGS += "-Wno-error=incompatible-pointer-types"

# Respect GI_DATA_ENABLED value when enabling vala-bindings:
# configure: error: GObject-Introspection must be enabled for Vala bindings
EXTRA_OECONF = "${@bb.utils.contains('GI_DATA_ENABLED', 'True', '--enable-vala-bindings', '--disable-vala-bindings', d)}"

FILES:${PN} += "${datadir}/telepathy \
                ${datadir}/dbus-1"
