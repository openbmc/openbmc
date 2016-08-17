SUMMARY = "File manager for the Xfce Desktop Environment"
SECTION = "x11"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "exo glib-2.0 gtk+ gdk-pixbuf libxfce4util libxfce4ui xfconf libsm dbus-glib startup-notification libnotify xfce4-panel udev"

inherit xfce

SRC_URI = "http://archive.xfce.org/src/xfce/${BPN}/${@'${PV}'[0:3]}/Thunar-${PV}.tar.bz2"
SRC_URI[md5sum] = "3089e1dca6e408641b07cd9c759dea5e"
SRC_URI[sha256sum] = "7e9d24067268900e5e44d3325e60a1a2b2f8f556ec238ec12574fbea15fdee8a"

S = "${WORKDIR}/Thunar-${PV}/"

PACKAGECONFIG ??= ""
PACKAGECONFIG[pcre] = "--enable-pcre,--disable-pcre,libpcre"

FILES_${PN} += " \
    ${libdir}/thunarx-2/* \
    ${libdir}/xfce4/panel/plugins/* \
    ${libdir}/Thunar/[Tt]hunar* \
    ${datadir}/appdata \
    ${datadir}/dbus-1 \
    ${datadir}/polkit-1 \
    ${datadir}/Thunar \
    ${datadir}/xfce4/panel/plugins/* \
"

FILES_${PN}-dbg += "${libdir}/thunarx-2/.debug/ \
                    ${libdir}/xfce4/panel/plugins/.debug/ \
                    ${libdir}/Thunar/.debug/"

RRECOMMENDS_${PN} = "gvfs gvfsd-trash"
