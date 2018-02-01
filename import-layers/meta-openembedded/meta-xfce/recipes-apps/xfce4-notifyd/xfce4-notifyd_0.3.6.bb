SUMMARY = "Easily themable notification daemon with transparency effects"
HOMEPAGE = "http://goodies.xfce.org/projects/applications/xfce4-notifyd"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "libxfce4util libxfce4ui xfconf gtk+ dbus dbus-glib libnotify \
    dbus-glib-native \
"

inherit xfce-app

SRC_URI[md5sum] = "19e602fa5e33afaf7563f069261ae1db"
SRC_URI[sha256sum] = "f4ca7c0dadd3d4cdf8cd3c8ae60ccea77b8cf409f8517161796364eb1d766cf9"

do_compile_prepend() {
    mkdir -p xfce4-notifyd
}

FILES_${PN} += " \
    ${systemd_user_unitdir} \
    ${libdir}/xfce4/notifyd \
    ${datadir}/themes \
    ${datadir}/dbus-1 \
"
