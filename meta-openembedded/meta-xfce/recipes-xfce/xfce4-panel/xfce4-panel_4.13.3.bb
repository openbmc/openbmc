SUMMARY = "Xfce4 Panel"
SECTION = "x11"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=26a8bd75d8f8498bdbbe64a27791d4ee"
DEPENDS = "libxfce4util garcon libxfce4ui xfconf exo gtk+ gtk+3 dbus cairo virtual/libx11 libxml2 libwnck3 vala-native"

inherit xfce gtk-doc gobject-introspection distro_features_check gtk-icon-cache remove-libtool

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[md5sum] = "0ccb448e3792949eb0ae9db8dc22789b"
SRC_URI[sha256sum] = "b467feb7ee9797ad12f56a152570e42a96b94ad63580d45536aabee282440ce8"
SRC_URI += " \
    file://0001-windowmenu-do-not-display-desktop-icon-when-no-windo.patch \
"

python populate_packages_prepend() {
    plugin_dir = d.expand('${libdir}/xfce4/panel/plugins/')
    plugin_name = d.expand('${PN}-plugin-%s')
    do_split_packages(d, plugin_dir, '^lib(.*).so$', plugin_name,
                      '${PN} plugin for %s', extra_depends='', prepend=True,
                      aux_files_pattern=['${datadir}/xfce4/panel/plugins/%s.desktop',
                                         '${sysconfdir}/xdg/xfce/panel/%s-*',
                                         '${datadir}/icons/hicolor/48x48/apps/*-%s.png',
                                         '${bindir}/*%s*'])
}

PACKAGES_DYNAMIC += "^${PN}-plugin-.*"

PACKAGES =+ "${PN}-gtk3"

FILES_${PN} += "${libdir}/xfce4/panel/migrate \
                ${libdir}/xfce4/panel/wrapper-1.0"

FILES_${PN}-dev += "${libdir}/xfce4/panel/plugins/*.la"

FILES_${PN}-gtk3 = " \
    ${libdir}/libxfce4panel-2.0${SOLIBS} \
    ${libdir}/xfce4/panel/wrapper-2.0 \
"
