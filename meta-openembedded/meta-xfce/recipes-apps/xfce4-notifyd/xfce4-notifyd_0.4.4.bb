SUMMARY = "Easily themable notification daemon with transparency effects"
HOMEPAGE = "http://goodies.xfce.org/projects/applications/xfce4-notifyd"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = " \
    dbus \
    dbus-glib \
    libnotify \
    libxfce4util \
    libxfce4ui \
    xfconf \
    xfce4-panel \
"

inherit xfce-app

SRC_URI[md5sum] = "9614d4f7c0790e8ab24e55896a44a8ac"
SRC_URI[sha256sum] = "090571acf94c423003426cb779fb23e8545c68bab6485563b589c7def8a21b55"

# Avoid trouble with other desktops e.g KDE which also ships dbus service named
# org.freedesktop.Notifications
EXTRA_OECONF = "--disable-dbus-start-daemon"

do_compile_prepend() {
    mkdir -p xfce4-notifyd xfce4-notifyd-config
}

FILES_${PN} += " \
    ${systemd_user_unitdir} \
    ${datadir}/xfce4 \
    ${datadir}/themes \
    ${datadir}/dbus-1 \
    ${libdir}/xfce4 \
"
