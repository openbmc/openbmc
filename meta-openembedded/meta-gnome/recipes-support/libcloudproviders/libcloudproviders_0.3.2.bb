SUMMARY = "libcloudproviders is a DBus API that allows cloud storage sync clients to expose their services."
LICENSE="LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e6a600fd5e1d9cbde2d983680233ad02"

GNOMEBASEBUILDCLASS = "meson"

DEPENDS = "glib-2.0"

inherit gnomebase gobject-introspection vala

SRC_URI[archive.sha256sum] = "24a9f3fffaf49f1d9d45d6ec35ba9f9e59a5a1040b51ce7835611131966c6819"
