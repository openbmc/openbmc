SUMMARY = "Library for sending desktop notifications to a notification daemon"
DESCRIPTION = "It sends desktop notifications to a notification daemon, as defined \
in the Desktop Notifications spec. These notifications can be used to inform \
the user about an event or display some form of information without getting \
in the user's way."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/libnotify"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/libnotify/issues"
SECTION = "libs"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"

DEPENDS = "glib-2.0 glib-2.0-native gdk-pixbuf"

PACKAGECONFIG ?= ""
PACKAGECONFIG[tests] = "-Dtests=true,-Dtests=false,gtk+3"

GIR_MESON_ENABLE_FLAG = "enabled"
GIR_MESON_DISABLE_FLAG = "disabled"
inherit gnomebase gi-docgen features_check gobject-introspection
# depends on gtk+3 if tests are enabled
ANY_OF_DISTRO_FEATURES = "${@bb.utils.contains('PACKAGECONFIG', 'tests', '${GTK3DISTROFEATURES}', '', d)}"

SRC_URI[archive.sha256sum] = "ee8f3ef946156ad3406fdf45feedbdcd932dbd211ab4f16f75eba4f36fb2f6c0"

EXTRA_OEMESON = "-Dman=false"

# there were times, we had two versions of libnotify (oe-core libnotify:0.6.x /
# meta-gnome libnotify3: 0.7.x)
PROVIDES += "libnotify3"
RPROVIDES:${PN} += "libnotify3"
RCONFLICTS:${PN} += "libnotify3"
RREPLACES:${PN} += "libnotify3"

# -7381 is specific to the NodeJS bindings
CVE_STATUS[CVE-2013-7381] = "cpe-incorrect: The issue is specific to the NodeJS bindings"
