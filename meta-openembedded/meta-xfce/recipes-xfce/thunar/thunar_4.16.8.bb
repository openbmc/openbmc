SUMMARY = "File manager for the Xfce Desktop Environment"
SECTION = "x11"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "exo gdk-pixbuf libxfce4ui libsm startup-notification libnotify xfce4-panel udev"

inherit xfce gobject-introspection features_check mime-xdg

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[sha256sum] = "038ca228d220cd0ba1b7b76465d8a51f2433ad1f74648d1d291daa0a24cb3195"

PACKAGECONFIG ??= ""
PACKAGECONFIG[pcre] = "--enable-pcre,--disable-pcre,libpcre"

FILES:${PN} += " \
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

RRECOMMENDS:${PN} = "gvfs gvfsd-trash"
