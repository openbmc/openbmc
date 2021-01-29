SUMMARY = "configuation database system"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"
SECTION = "x11/gnome"

SRC_URI[archive.sha256sum] = "45f60f41330d27715cce1315af123f94f1c2cdedb68b6bed3b309866eec44f58"

DEPENDS = "dbus glib-2.0 intltool-native"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase bash-completion vala

# I/O error : Attempt to load network entity http://docbook.sourceforge.net/release/xsl/current/manpages/docbook.xsl
EXTRA_OEMESON = "-Dman=false"
# no bash-completion for native
EXTRA_OEMESON_append_class-native = " -Dbash_completion=false"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${libdir}/gio/modules/*.so \
"

BBCLASSEXTEND = "native"
