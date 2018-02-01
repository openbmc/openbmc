SUMMARY = "Xfce configuration daemon and utilities"
SECTION = "x11/wm"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"
DEPENDS = "dbus-glib libxfce4util perl intltool-native xfce4-dev-tools-native"

inherit xfce gtk-doc

EXTRA_OECONF += "PERL=${STAGING_DIR_TARGET}/usr/bin/perl"

SRC_URI[md5sum] = "20dc8d2bfd80ba136bf4964021b32757"
SRC_URI[sha256sum] = "35f48564e5694faa54fdc180cd3268e16fc2352946a89a3c2fc12cbe400ada36"

FILES_${PN} += "${libdir}/xfce4/xfconf/xfconfd \
                ${datadir}/dbus-1/services/org.xfce.Xfconf.service"
