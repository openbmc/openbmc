SUMMARY = "configuation database system"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"
SECTION = "x11/gnome"

SRC_URI[archive.md5sum] = "1a50c988d9f0206f74a44f7c6d09cead"
SRC_URI[archive.sha256sum] = "9fe6bb22191fc2a036ad86fd8e7d165e9983c687b9fedccf85d46c799301da2d"

DEPENDS = "dbus glib-2.0 intltool-native"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase bash-completion vala

# I/O error : Attempt to load network entity http://docbook.sourceforge.net/release/xsl/current/manpages/docbook.xsl
EXTRA_OEMESON = "-Dman=false"
# no bash-completion for native
EXTRA_OEMESON_append_class-native = "-Dbash_completion=false"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${libdir}/gio/modules/*.so \
"

BBCLASSEXTEND = "native"
