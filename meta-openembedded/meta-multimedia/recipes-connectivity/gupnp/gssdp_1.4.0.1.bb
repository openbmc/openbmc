SUMMARY = "Resource discovery and announcement over SSDP"
DESCRIPTION = "GSSDP implements resource discovery and announcement over SSDP \
               (Simpe Service Discovery Protocol)."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/gssdp/"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/gssdp/-/issues"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "${GNOME_MIRROR}/${BPN}/1.4/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "8676849d57fb822b8728856dbadebf3867f89ee47a0ec47a20045d011f431582"

GTKDOC_MESON_OPTION = 'gtk_doc'

DEPENDS = " \
    glib-2.0 \
    libsoup-2.4 \
"

inherit meson pkgconfig gobject-introspection vala gtk-doc features_check

SNIFFER = "${@bb.utils.contains("BBFILE_COLLECTIONS", "gnome-layer", "sniffer", "", d)}"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'opengl', "${SNIFFER}", "", d)}"

PACKAGECONFIG[sniffer] = "-Dsniffer=true,-Dsniffer=false,gtk4,"

REQUIRED_DISTRO_FEATURES = "${@bb.utils.contains('PACKAGECONFIG', 'sniffer', 'opengl', '', d)}"

PACKAGES =+ "gssdp-tools"

FILES:gssdp-tools = "${bindir}/gssdp* ${datadir}/gssdp/*.glade"
