SUMMARY = "libsecret is a library for storing and retrieving passwords and other secrets"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=23c2a5e0106b99d75238986559bb5fc6"

inherit gnomebase gtk-doc vala gobject-introspection manpages

DEPENDS += "glib-2.0 libgcrypt gettext-native intltool-native"

PACKAGECONFIG[manpages] = "--enable-manpages, --disable-manpages, libxslt-native xmlto-native"

SRC_URI[archive.md5sum] = "c6cf132a56bd346fbf49a43abb02e5c2"
SRC_URI[archive.sha256sum] = "5efbc890ba41a323ffe0599cd260fd12bd8eb62a04aa1bd1b2762575d253d66f"

# http://errors.yoctoproject.org/Errors/Details/20228/
ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"
ARM_INSTRUCTION_SET_armv6 = "arm"

# vapigen.m4 bundled with the tarball does not yet have our cross-compilation fixes
do_configure_prepend() {
    rm -f ${S}/build/m4/vapigen.m4
}
