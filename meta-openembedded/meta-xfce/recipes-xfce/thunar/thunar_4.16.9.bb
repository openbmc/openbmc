SUMMARY = "File manager for the Xfce Desktop Environment"
SECTION = "x11"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "libxml-parser-perl-native exo gdk-pixbuf libxfce4ui libsm startup-notification libnotify xfce4-panel udev"

inherit xfce gobject-introspection features_check mime-xdg perlnative

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[sha256sum] = "520bf318eef8bc792db38ae4af343b648f87d4b8d66f5b5d6f092e15264ee5af"

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
