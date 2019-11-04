SUMMARY = "libsecret is a library for storing and retrieving passwords and other secrets"
DESCRIPTION = "A GObject-based library for accessing the Secret Service API of \
the freedesktop.org project, a cross-desktop effort to access passwords, \
tokens and other types of secrets. libsecret provides a convenient wrapper \
for these methods so consumers do not have to call the low-level DBus methods."
LICENSE = "LGPLv2.1"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/libsecret/issues"
LIC_FILES_CHKSUM = "file://COPYING;md5=23c2a5e0106b99d75238986559bb5fc6"

inherit gnomebase gtk-doc vala gobject-introspection manpages

DEPENDS += "glib-2.0 libgcrypt gettext-native"

PACKAGECONFIG[manpages] = "--enable-manpages, --disable-manpages, libxslt-native xmlto-native"

SRC_URI[archive.md5sum] = "ea673119c00570d6434f8fd3636f1eb8"
SRC_URI[archive.sha256sum] = "8583e10179456ae2c83075d95455f156dc08db6278b32bf4bd61819335a30e3a"

# http://errors.yoctoproject.org/Errors/Details/20228/
ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"
ARM_INSTRUCTION_SET_armv6 = "arm"

# vapigen.m4 bundled with the tarball does not yet have our cross-compilation fixes
do_configure_prepend() {
    rm -f ${S}/build/m4/vapigen.m4
}
