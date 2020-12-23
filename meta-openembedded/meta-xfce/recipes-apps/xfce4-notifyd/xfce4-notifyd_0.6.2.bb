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

SRC_URI[sha256sum] = "19ab84c6665c7819998f2269322d53f462c30963ce26042df23ae525e7d16545"

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
