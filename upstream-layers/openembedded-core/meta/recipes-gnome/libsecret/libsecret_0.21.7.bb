SUMMARY = "libsecret is a library for storing and retrieving passwords and other secrets"
DESCRIPTION = "A GObject-based library for accessing the Secret Service API of \
the freedesktop.org project, a cross-desktop effort to access passwords, \
tokens and other types of secrets. libsecret provides a convenient wrapper \
for these methods so consumers do not have to call the low-level DBus methods."
LICENSE = "LGPL-2.1-only"
HOMEPAGE = "https://github.com/GNOME/libsecret"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/libsecret/issues"
LIC_FILES_CHKSUM = "file://COPYING;md5=23c2a5e0106b99d75238986559bb5fc6"

inherit gnomebase gi-docgen vala gobject-introspection manpages

DEPENDS += "glib-2.0 libgcrypt gettext-native"

SRC_URI += "file://0001-meson-add-option-to-disable-pam-tests.patch"
SRC_URI[archive.sha256sum] = "6b452e4750590a2b5617adc40026f28d2f4903de15f1250e1d1c40bfd68ed55e"

EXTRA_OEMESON += "-Dpam-tests=false"

GTKDOC_MESON_OPTION = 'gtk_doc'

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)}"
PACKAGECONFIG[manpages] = "-Dmanpage=true,-Dmanpage=false,libxslt-native xmlto-native"
PACKAGECONFIG[pam] = "-Dpam=true,-Dpam=false,libpam"

inherit bash-completion

# http://errors.yoctoproject.org/Errors/Details/20228/
ARM_INSTRUCTION_SET:armv4 = "arm"
ARM_INSTRUCTION_SET:armv5 = "arm"
ARM_INSTRUCTION_SET:armv6 = "arm"
