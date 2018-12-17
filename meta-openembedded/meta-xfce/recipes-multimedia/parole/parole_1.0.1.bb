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

SRC_URI[md5sum] = "46fe86bbe0c4aa02c53244d66e62e90c"
SRC_URI[sha256sum] = "8ad2931fdb35415cc3d7551b5f2207bfaa1aba15545accbacbb4984cdabd7099"

RDEPENDS_${PN} += "gstreamer1.0-plugins-good"

EXTRA_OECONF = "--disable-gtk-doc"

PACKAGECONFIG ??= "notify"
PACKAGECONFIG[clutter] = "--enable-clutter, --disable-clutter, clutter"
PACKAGECONFIG[notify] = "--enable-notify-plugin, --disable-notify-plugin, libnotify"

FILES_${PN} += " \
    ${datadir}/appdata \
    ${libdir}/parole-0/*.so \
"

FILES_${PN}-dev += "${libdir}/parole-0/*.la"
