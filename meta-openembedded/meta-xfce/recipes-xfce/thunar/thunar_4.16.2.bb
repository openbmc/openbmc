SUMMARY = "File manager for the Xfce Desktop Environment"
SECTION = "x11"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "exo gdk-pixbuf libxfce4ui libsm startup-notification libnotify xfce4-panel udev"

inherit xfce gobject-introspection features_check mime-xdg

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[sha256sum] = "67a90b98c436192f9aa6cd18a22a089e713007864aebfa65f36f121c86ba7add"

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
