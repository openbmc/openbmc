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

SRC_URI[md5sum] = "1724fcb635bbab44c9f62c69734e446d"
SRC_URI[sha256sum] = "c1d126a81604ff967e4655f06427763bf559c338ba629c95cb9aa7f5d6dc8ba6"

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
