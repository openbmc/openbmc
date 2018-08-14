SUMMARY = "Telepathy Framework glib-base helper library"
DESCRIPTION = "Telepathy Framework: GLib-based helper library for connection managers"
HOMEPAGE = "http://telepathy.freedesktop.org/wiki/"
DEPENDS = "glib-2.0 dbus hostpython-runtime-native dbus-native dbus-glib libxslt-native"
LICENSE = "LGPLv2.1+"

SRC_URI = "http://telepathy.freedesktop.org/releases/telepathy-glib/${BP}.tar.gz"
SRC_URI[md5sum] = "93c429e37750b25dcf8de86bb514664f"
SRC_URI[sha256sum] = "ae0002134991217f42e503c43dea7817853afc18863b913744d51ffa029818cf"

LIC_FILES_CHKSUM = "file://COPYING;md5=e413d83db6ee8f2c8e6055719096a48e"

inherit autotools pkgconfig gettext gobject-introspection

FILES_${PN} += "${datadir}/telepathy \
                ${datadir}/dbus-1"
