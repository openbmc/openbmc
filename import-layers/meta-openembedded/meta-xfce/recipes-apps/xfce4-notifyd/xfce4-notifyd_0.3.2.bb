SUMMARY = "Easily themable notification daemon with transparency effects"
HOMEPAGE = "http://goodies.xfce.org/projects/applications/xfce4-notifyd"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "libxfce4util libxfce4ui xfconf gtk+ dbus dbus-glib libnotify"

inherit xfce-app

SRC_URI[md5sum] = "9ee9bd823710c8753cb99f5a8028d528"
SRC_URI[sha256sum] = "c554db55d9d759c32115e9e5da029bd68b07628438ef2bac7ae4b458567c85a4"

do_compile_prepend() {
    mkdir -p xfce4-notifyd
}

FILES_${PN} += " \
    ${systemd_user_unitdir} \
    ${libdir}/xfce4/notifyd \
    ${datadir}/themes \
    ${datadir}/dbus-1 \
"
