SUMMARY = "File manager for the Xfce Desktop Environment"
SECTION = "x11"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "exo gdk-pixbuf libxfce4ui libsm startup-notification libnotify xfce4-panel udev"

inherit xfce gobject-introspection features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[md5sum] = "85c7394d82542d5a023f1f5baef109d8"
SRC_URI[sha256sum] = "3d448d81c0e49efbaa5ae6bf34ac3c88a614178a1127afaee6b3a1b5ee12e709"

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
