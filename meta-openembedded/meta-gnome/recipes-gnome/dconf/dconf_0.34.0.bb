SUMMARY = "configuation database system"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"
SECTION = "x11/gnome"

SRC_URI[archive.md5sum] = "a3ab18ed51a0494a1c8436fef20cc1b0"
SRC_URI[archive.sha256sum] = "943a94ab16121de5580ceaed2605b87444d1bca1c6cd8beefb778bcb0aa2da52"

DEPENDS = "dbus glib-2.0 intltool-native"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase bash-completion vala

SRC_URI += "file://fix-meson-0.52.patch"

# I/O error : Attempt to load network entity http://docbook.sourceforge.net/release/xsl/current/manpages/docbook.xsl
EXTRA_OEMESON = "-Dman=false"
# no bash-completion for native
EXTRA_OEMESON_append_class-native = "-Dbash_completion=false"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${libdir}/gio/modules/*.so \
"

BBCLASSEXTEND = "native"
