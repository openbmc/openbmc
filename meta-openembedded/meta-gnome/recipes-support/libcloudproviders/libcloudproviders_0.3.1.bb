SUMMARY = "libcloudproviders is a DBus API that allows cloud storage sync clients to expose their services."
LICENSE="LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e6a600fd5e1d9cbde2d983680233ad02"

GNOMEBASEBUILDCLASS = "meson"

DEPENDS = "glib-2.0"

inherit gnomebase gobject-introspection vala

SRC_URI[archive.sha256sum] = "4763213ca1e1fe30d422f5ae3b4f02a454f63414c1860ad142d6385f89d05929"
