SUMMARY = "Telepathy Framework glib-base helper library"
DESCRIPTION = "Telepathy Framework: GLib-based helper library for connection managers"
HOMEPAGE = "http://telepathy.freedesktop.org/wiki/"
DEPENDS = "glib-2.0 dbus hostpython-runtime-native dbus-native dbus-glib libxslt-native"
LICENSE = "LGPL-2.1-or-later"

SRC_URI = "http://telepathy.freedesktop.org/releases/telepathy-glib/${BP}.tar.gz"
SRC_URI[sha256sum] = "9e0df1d8f857e0270cf0b32e2d1ca5a24aa7282873361785d573f72ad7f7d5eb"

LIC_FILES_CHKSUM = "file://COPYING;md5=e413d83db6ee8f2c8e6055719096a48e"

inherit autotools pkgconfig gettext gobject-introspection vala gtk-doc

# Respect GI_DATA_ENABLED value when enabling vala-bindings:
# configure: error: GObject-Introspection must be enabled for Vala bindings
EXTRA_OECONF = "${@bb.utils.contains('GI_DATA_ENABLED', 'True', '--enable-vala-bindings', '--disable-vala-bindings', d)}"

FILES:${PN} += "${datadir}/telepathy \
                ${datadir}/dbus-1"
