SUMMARY = "Xfce configuration daemon and utilities"
SECTION = "x11/wm"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"
DEPENDS = "dbus-glib libxfce4util perl intltool-native xfce4-dev-tools-native"

inherit xfce gtk-doc

EXTRA_OECONF += "PERL=${STAGING_DIR_TARGET}/usr/bin/perl"

SRC_URI[md5sum] = "da19df12dbc494c8a4da3974e376d639"
SRC_URI[sha256sum] = "d1a3d442dae188b5a7380b5815377e5488578cdafb03ae363e9426e3b01185df"

FILES_${PN} += "${libdir}/xfce4/xfconf/xfconfd \
                ${libdir}/gio/modules/libxfconfgsettingsbackend.so \
                ${datadir}/dbus-1/services/org.xfce.Xfconf.service"

FILES_${PN}-dev += "${libdir}/gio/modules/libxfconfgsettingsbackend.la"
