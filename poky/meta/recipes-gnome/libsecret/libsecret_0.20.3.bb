SUMMARY = "libsecret is a library for storing and retrieving passwords and other secrets"
DESCRIPTION = "A GObject-based library for accessing the Secret Service API of \
the freedesktop.org project, a cross-desktop effort to access passwords, \
tokens and other types of secrets. libsecret provides a convenient wrapper \
for these methods so consumers do not have to call the low-level DBus methods."
LICENSE = "LGPLv2.1"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/libsecret/issues"
LIC_FILES_CHKSUM = "file://COPYING;md5=23c2a5e0106b99d75238986559bb5fc6"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gtk-doc vala gobject-introspection manpages

DEPENDS += "glib-2.0 libgcrypt gettext-native"

SRC_URI += "file://0001-meson-add-option-introspection.patch"

SRC_URI[archive.md5sum] = "47c3fdfeb111a87b509ad271e4a6f496"
SRC_URI[archive.sha256sum] = "4fcb3c56f8ac4ab9c75b66901fb0104ec7f22aa9a012315a14c0d6dffa5290e4"

GTKDOC_MESON_OPTION = 'gtk_doc'

PACKAGECONFIG[manpages] = "-Dmanpage=true,-Dmanpage=false,libxslt-native xmlto-native"

# http://errors.yoctoproject.org/Errors/Details/20228/
ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"
ARM_INSTRUCTION_SET_armv6 = "arm"
