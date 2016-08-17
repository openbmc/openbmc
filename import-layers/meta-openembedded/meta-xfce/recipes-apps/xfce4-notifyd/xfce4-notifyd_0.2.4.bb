SUMMARY = "Easily themable notification daemon with transparency effects"
HOMEPAGE = "http://goodies.xfce.org/projects/applications/xfce4-notifyd"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "libxfce4util libxfce4ui xfconf gtk+ dbus dbus-glib libnotify"

inherit xfce-app

SRC_URI[md5sum] = "094be6f29206aac8299f27084e284e88"
SRC_URI[sha256sum] = "8c7ed62f9496816d1391281f77d1b32216f9bf6fd22fbe4f6f3f4e07a6bbced0"

do_compile_prepend() {
	mkdir -p xfce4-notifyd
}

FILES_${PN} += " \
    ${libdir}/xfce4/notifyd \
    ${datadir}/themes \
    ${datadir}/dbus-1 \
"
