SUMMARY = "Xfce configuration daemon and utilities"
SECTION = "x11/wm"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"
DEPENDS = "libxfce4util perl intltool-native xfce4-dev-tools-native"

inherit xfce gtk-doc gobject-introspection

EXTRA_OECONF += "PERL=${STAGING_DIR_TARGET}/usr/bin/perl"

SRC_URI[md5sum] = "cb51a59e2a89d05232f825ad8c74a7c0"
SRC_URI[sha256sum] = "b893e0a329aee00902fec2f0509f56916c9dcc7844e1b1f9e3c7399458290d59"

FILES_${PN} += "${libdir}/xfce4/xfconf/xfconfd \
                ${libdir}/gio/modules/libxfconfgsettingsbackend.so \
                ${datadir}/dbus-1/services/org.xfce.Xfconf.service"

FILES_${PN}-dev += "${libdir}/gio/modules/libxfconfgsettingsbackend.la"

PACKAGECONFIG[gsettings-backend] = "--enable-gsettings-backend,--disable-gsettings-backend,"
