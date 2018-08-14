SUMMARY = "Library for sending desktop notifications to a notification daemon"
HOMEPAGE = "http://www.gnome.org"
SECTION = "libs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"

DEPENDS = "dbus gtk+3 dbus-glib"

inherit gnomebase gtk-doc distro_features_check gobject-introspection
# depends on gtk+3
ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI[archive.md5sum] = "e9d911f6a22435e0b922f2fe71212b59"
SRC_URI[archive.sha256sum] = "9cb4ce315b2655860c524d46b56010874214ec27e854086c1a1d0260137efc04"

# there were times, we had two versions of libnotify (oe-core libnotify:0.6.x /
# meta-gnome libnotify3: 0.7.x)
PROVIDES += "libnotify3"
RPROVIDES_${PN} += "libnotify3"
RCONFLICTS_${PN} += "libnotify3"
RREPLACES_${PN} += "libnotify3"
