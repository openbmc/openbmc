DESCRIPTION = "Parole is a modern simple media player based on the GStreamer framework"
HOMEPAGE = "http://goodies.xfce.org/projects/applications/parole"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

inherit xfce-app gtk-doc mime

DEPENDS += " \
    glib-2.0 \
    dbus-glib \
    gtk+3 \
    \
    xfce4-dev-tools-native \
    libxfce4util \
    libxfce4ui \
    xfconf \
    \
    gstreamer1.0-plugins-base \
    taglib \
"

SRC_URI[md5sum] = "d00d3ca571900826bf5e1f6986e42992"
SRC_URI[sha256sum] = "6666b335aeb690fb527f77b62c322baf34834b593659fdcd21d21ed3f1e14010"

RDEPENDS_${PN} += "gstreamer1.0-plugins-good"

EXTRA_OECONF = "--disable-gtk-doc"

PACKAGECONFIG ??= "notify"
PACKAGECONFIG[clutter] = "--enable-clutter, --disable-clutter, clutter"
PACKAGECONFIG[notify] = "--enable-notify-plugin, --disable-notify-plugin, libnotify"

FILES_${PN} += " \
    ${datadir}/appdata \
    ${libdir}/parole-0/*.so \
"
FILES_${PN}-dbg += "${libdir}/parole-0/.debug"
FILES_${PN}-dev += "${libdir}/parole-0/*.la"
