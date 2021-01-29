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

SRC_URI[sha256sum] = "0d305ad8ccd3974d6b632f74325b1b8a39304c905c6b405b70f52c4cfd55a7e7"

RDEPENDS_${PN} += "gstreamer1.0-plugins-good"

EXTRA_OECONF = "--disable-gtk-doc"

PACKAGECONFIG ??= "notify"
PACKAGECONFIG[clutter] = "--enable-clutter, --disable-clutter, clutter"
PACKAGECONFIG[notify] = "--enable-notify-plugin, --disable-notify-plugin, libnotify"

FILES_${PN} += " \
    ${datadir}/metainfo \
    ${libdir}/parole-0/*.so \
"
