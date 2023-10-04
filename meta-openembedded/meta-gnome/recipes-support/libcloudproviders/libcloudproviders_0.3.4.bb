SUMMARY = "libcloudproviders is a DBus API that allows cloud storage sync clients to expose their services."
LICENSE="LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e6a600fd5e1d9cbde2d983680233ad02"

GNOMEBASEBUILDCLASS = "meson"

DEPENDS = "glib-2.0"

inherit gnomebase gobject-introspection vala

SRC_URI[archive.sha256sum] = "e3d7160c3e45fe3216c07c7988bb4fc81397c32d1fc6113af0edfe8632039f65"
