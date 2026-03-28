SUMMARY = "Xfce4 Panel"
HOMEPAGE = "https://docs.xfce.org/xfce/xfce4-panel/start"
SECTION = "x11"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=26a8bd75d8f8498bdbbe64a27791d4ee"
DEPENDS = "garcon exo gtk+3 cairo virtual/libx11 libxfce4windowing libxml2 \
           libwnck3 vala-native \
           "

XFCE_COMPRESS_TYPE = "xz"
XFCEBASEBUILDCLASS = "meson"
GTKDOC_MESON_OPTION = "gtk-doc"

inherit xfce gtk-doc gobject-introspection features_check mime-xdg

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += " \
    file://0001-windowmenu-do-not-display-desktop-icon-when-no-windo.patch \
    file://0002-use-lxdm-to-replace-dm-tool.patch \
    file://0001-build-Do-not-display-full-path-in-generated-headers.patch \
"
SRC_URI[sha256sum] = "c940dae515bef4af08a126011d2fd873ce99e815157ba8cef5332195bb60e51e"

EXTRA_OEMESON += "-Dvala=disabled"

python populate_packages:prepend() {
    plugin_dir = d.expand('${libdir}/xfce4/panel/plugins/')
    plugin_name = d.expand('${PN}-plugin-%s')
    do_split_packages(d, plugin_dir, r'^lib(.*)\.so$', plugin_name,
                      '${PN} plugin for %s', extra_depends='', prepend=True,
                      aux_files_pattern=['${datadir}/xfce4/panel/plugins/%s.desktop',
                                         '${sysconfdir}/xdg/xfce/panel/%s-*',
                                         '${datadir}/icons/hicolor/48x48/apps/*-%s.png',
                                         '${bindir}/*%s*'])
}

PACKAGES_DYNAMIC += "^${PN}-plugin-.*"

PACKAGES =+ "${PN}-gtk3"

FILES:${PN} += "${libdir}/xfce4/panel/migrate \
                ${libdir}/xfce4/panel/wrapper-1.0"

FILES:${PN}-dev += "${libdir}/xfce4/panel/plugins/*.la"

FILES:${PN}-gtk3 = " \
    ${libdir}/libxfce4panel-2.0${SOLIBS} \
    ${libdir}/xfce4/panel/wrapper-2.0 \
"
