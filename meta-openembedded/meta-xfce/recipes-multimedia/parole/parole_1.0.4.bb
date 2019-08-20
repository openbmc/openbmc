DESCRIPTION = "Parole is a modern simple media player based on the GStreamer framework"
HOMEPAGE = "https://docs.xfce.org/apps/parole/start"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

inherit xfce-app gtk-doc mime

DEPENDS += " \
    dbus-glib \
    xfce4-dev-tools-native \
    libxfce4util \
    libxfce4ui \
    xfconf \
    \
    gstreamer1.0-plugins-base \
    taglib \
"

SRC_URI[md5sum] = "c23621eb44df292f828e86074d4e719d"
SRC_URI[sha256sum] = "e92b8ec369e53d921b47d2473c2e2a1e9e04d3c5d536d419abdff40e5e136dc1"

RDEPENDS_${PN} += "gstreamer1.0-plugins-good"

EXTRA_OECONF = "--disable-gtk-doc"

PACKAGECONFIG ??= "notify"
PACKAGECONFIG[clutter] = "--enable-clutter, --disable-clutter, clutter"
PACKAGECONFIG[notify] = "--enable-notify-plugin, --disable-notify-plugin, libnotify"

FILES_${PN} += " \
    ${datadir}/metainfo \
    ${libdir}/parole-0/*.so \
"
