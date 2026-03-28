SUMMARY = "File manager for the Xfce Desktop Environment"
HOMEPAGE = "https://docs.xfce.org/xfce/thunar/start"
SECTION = "x11"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "libxml-parser-perl-native libxslt-native docbook-xsl-stylesheets-native exo gdk-pixbuf libxfce4ui libnotify xfce4-panel udev"

XFCE_COMPRESS_TYPE = "xz"
XFCEBASEBUILDCLASS = "meson"
GTKDOC_MESON_OPTION = "gtk-doc"

inherit xfce gobject-introspection features_check mime-xdg perlnative gtk-doc

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[sha256sum] = "85f2c7fba6e891eb0df04b94139d519778130ecde95a6e629ac611d8cc9c6a7c"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
PACKAGECONFIG[pcre] = "-Dpcre2=enabled,-Dpcre2=disabled,libpcre2"
PACKAGECONFIG[x11] = "-Dx11=enabled -Dsession-management=enabled,-Dx11=disabled,libsm startup-notification"

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
