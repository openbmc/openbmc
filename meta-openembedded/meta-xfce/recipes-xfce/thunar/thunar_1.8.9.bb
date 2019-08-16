SUMMARY = "File manager for the Xfce Desktop Environment"
SECTION = "x11"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "exo gdk-pixbuf libxfce4ui libsm startup-notification libnotify xfce4-panel udev"

inherit xfce gobject-introspection distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "http://archive.xfce.org/src/xfce/${BPN}/${@'${PV}'[0:3]}/Thunar-${PV}.tar.bz2"
SRC_URI[md5sum] = "fd1166e879294e4490d5fa9dccfdd9da"
SRC_URI[sha256sum] = "7a758e7ac03501c520c304f8845353315c954d429b17d591b8eea8b14f1350b9"

S = "${WORKDIR}/Thunar-${PV}/"

PACKAGECONFIG ??= ""
PACKAGECONFIG[pcre] = "--enable-pcre,--disable-pcre,libpcre"

FILES_${PN} += " \
    ${libdir}/thunarx-3/* \
    ${libdir}/xfce4/panel/plugins/* \
    ${libdir}/Thunar/[Tt]hunar* \
    ${systemd_user_unitdir} \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${datadir}/polkit-1 \
    ${datadir}/Thunar \
    ${datadir}/xfce4/panel/plugins/* \
"

RRECOMMENDS_${PN} = "gvfs gvfsd-trash"
