SUMMARY = "configuation database system"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"
SECTION = "x11/gnome"

SRC_URI[archive.sha256sum] = "16a47e49a58156dbb96578e1708325299e4c19eea9be128d5bd12fd0963d6c36"

DEPENDS = "dbus glib-2.0 intltool-native"


inherit gnomebase bash-completion vala

# I/O error : Attempt to load network entity http://docbook.sourceforge.net/release/xsl/current/manpages/docbook.xsl
EXTRA_OEMESON = "-Dman=false"
# no bash-completion for native
EXTRA_OEMESON:append:class-native = " -Dbash_completion=false"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${systemd_user_unitdir} \
    ${libdir}/gio/modules/*.so \
"

BBCLASSEXTEND = "native"
