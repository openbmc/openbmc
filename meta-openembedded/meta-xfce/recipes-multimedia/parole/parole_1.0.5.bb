DESCRIPTION = "Parole is a modern simple media player based on the GStreamer framework"
HOMEPAGE = "https://docs.xfce.org/apps/parole/start"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

inherit xfce-app gtk-doc mime mime-xdg

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

SRC_URI[md5sum] = "74fcde5da018c011e5febd9649817c05"
SRC_URI[sha256sum] = "1adb4bd96c4cc4b4a79eeafe1316e170f506426e3737e8ba8898f7ea6bec572a"

RDEPENDS_${PN} += "gstreamer1.0-plugins-good"

EXTRA_OECONF = "--disable-gtk-doc"

PACKAGECONFIG ??= "notify"
PACKAGECONFIG[clutter] = "--enable-clutter, --disable-clutter, clutter"
PACKAGECONFIG[notify] = "--enable-notify-plugin, --disable-notify-plugin, libnotify"

FILES_${PN} += " \
    ${datadir}/metainfo \
    ${libdir}/parole-0/*.so \
"
