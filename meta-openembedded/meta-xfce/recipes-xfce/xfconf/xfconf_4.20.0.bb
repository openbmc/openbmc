SUMMARY = "Xfce configuration daemon and utilities"
SECTION = "x11/wm"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"
DEPENDS = "libxfce4util perl intltool-native xfce4-dev-tools-native"

inherit xfce gtk-doc gobject-introspection bash-completion vala

EXTRA_OECONF += "GDBUS_CODEGEN=${STAGING_BINDIR_NATIVE}/gdbus-codegen"

SRC_URI[sha256sum] = "8bc43c60f1716b13cf35fc899e2a36ea9c6cdc3478a8f051220eef0f53567efd"

FILES:${PN} += "${libdir}/xfce4/xfconf/xfconfd \
                ${libdir}/gio/modules/libxfconfgsettingsbackend.so \
                ${datadir}/dbus-1/services/org.xfce.Xfconf.service \
                ${systemd_user_unitdir}/xfconfd.service \
                "

FILES:${PN}-dev += "${libdir}/gio/modules/libxfconfgsettingsbackend.la"

PACKAGECONFIG[gsettings-backend] = "--enable-gsettings-backend,--disable-gsettings-backend,"
