SUMMARY = "File manager for the Xfce Desktop Environment"
SECTION = "x11"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "libxml-parser-perl-native exo gdk-pixbuf libxfce4ui libnotify xfce4-panel udev"

inherit xfce gobject-introspection features_check mime-xdg perlnative

# xfce4 depends on libwnck3, gtk+3 and libepoxy need to be built with x11 PACKAGECONFIG.
# cairo would at least needed to be built with xlib.
ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI[sha256sum] = "684f1422c4d92b00db477ecaa2a0ed38ce688e2a567e5d70b7cf66fc6dd6fd18"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
PACKAGECONFIG[pcre] = "--enable-pcre2,--disable-pcre2,libpcre2"
PACKAGECONFIG[x11] = ",,libsm startup-notification"

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

RRECOMMENDS:${PN} = " \
    gvfs \
    gvfsd-trash \
    ${@bb.utils.contains('DISTRO_FEATURES', 'dbus', 'tumbler', '', d)} \
"
