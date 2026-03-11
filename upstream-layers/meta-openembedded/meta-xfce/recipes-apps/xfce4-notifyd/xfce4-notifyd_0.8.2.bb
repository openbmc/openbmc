SUMMARY = "Easily themable notification daemon with transparency effects"
HOMEPAGE = "https://docs.xfce.org/apps/notifyd/start"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = " \
    glib-2.0-native \
    libnotify \
    libxfce4util \
    libxfce4ui \
    xfconf \
    xfce4-panel \
    python3-packaging-native \
"

inherit xfce-app
inherit python3native

SRC_URI:append = " file://xfce4-notifyd-get-var-abs-path.patch"

SRC_URI[sha256sum] = "e3a28adb08daa1411135142a0d421e4d6050c4035a4e513a673a59460ff2ae84"

# Avoid trouble with other desktops e.g KDE which also ships dbus service named
# org.freedesktop.Notifications
EXTRA_OECONF = "--disable-dbus-start-daemon"

do_compile:prepend() {
    mkdir -p xfce4-notifyd xfce4-notifyd-config
}

FILES:${PN} += " \
    ${systemd_user_unitdir} \
    ${datadir}/xfce4 \
    ${datadir}/themes \
    ${datadir}/dbus-1 \
    ${libdir}/xfce4 \
"
