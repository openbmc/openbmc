SUMMARY = "Library for sending desktop notifications to a notification daemon"
DESCRIPTION = "It sends desktop notifications to a notification daemon, as defined \
in the Desktop Notifications spec. These notifications can be used to inform \
the user about an event or display some form of information without getting \
in the user's way."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/libnotify"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/libnotify/issues"
SECTION = "libs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"

DEPENDS = "dbus gtk+3 glib-2.0"

GNOMEBASEBUILDCLASS = "meson"
GTKDOC_MESON_OPTION = "gtk_doc"
GIR_MESON_ENABLE_FLAG = "enabled"
GIR_MESON_DISABLE_FLAG = "disabled"
inherit gnomebase gtk-doc features_check gobject-introspection
# depends on gtk+3
ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI[archive.md5sum] = "ccd9c53364174cc8d13e18a1988faa76"
SRC_URI[archive.sha256sum] = "66c0517ed16df7af258e83208faaf5069727dfd66995c4bbc51c16954d674761"

EXTRA_OEMESON = "-Dman=false"

# there were times, we had two versions of libnotify (oe-core libnotify:0.6.x /
# meta-gnome libnotify3: 0.7.x)
PROVIDES += "libnotify3"
RPROVIDES_${PN} += "libnotify3"
RCONFLICTS_${PN} += "libnotify3"
RREPLACES_${PN} += "libnotify3"
