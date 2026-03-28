SUMMARY = "Xfce configuration daemon and utilities"
HOMEPAGE = "https://docs.xfce.org/xfce/xfconf/start"
SECTION = "x11/wm"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"
DEPENDS = "libxfce4util perl intltool-native xfce4-dev-tools-native"

XFCE_COMPRESS_TYPE = "xz"
XFCEBASEBUILDCLASS = "meson"
GTKDOC_MESON_OPTION = "gtk-doc"

inherit xfce gtk-doc gobject-introspection bash-completion vala

SRC_URI += "file://0001-build-Make-sure-gdbus-headers-are-generated-before-i.patch"
SRC_URI[sha256sum] = "99aa4366e909ba7b9f746aba48b610b9e9d2933aeb283c7fa5f37a7c3dc7a3a6"

FILES:${PN} += "${libdir}/xfce4/xfconf/xfconfd \
                ${libdir}/gio/modules/libxfconfgsettingsbackend.so \
                ${datadir}/dbus-1/services/org.xfce.Xfconf.service \
                ${systemd_user_unitdir}/xfconfd.service \
                "

PACKAGECONFIG[gsettings-backend] = "-Dgsettings-backend=true,-Dgsettings-backend=false,"

