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

SRC_URI[md5sum] = "361e3059f1263c76a3711db2c7c1a97b"
SRC_URI[sha256sum] = "4b216f5200490f8d2a9bf1b3fcd9a8b20834c95249bf13b9170c82e1fcbd80f4"

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
