SUMMARY = "openbox Window Manager"
SECTION = "x11/wm"
DEPENDS = "glib-2.0 pango libxml2 virtual/libx11 librsvg gdk-pixbuf"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = " \
    http://icculus.org/openbox/releases/openbox-${PV}.tar.gz \
    file://0001-Makefile.am-avoid-race-when-creating-autostart-direc.patch \
    file://0001-openbox-xdg-autostart-convert-to-python3.patch \
    file://0001-Fix-list-traversal-issue-in-client_calc_layer.patch \
"

SRC_URI[md5sum] = "b72794996c6a3ad94634727b95f9d204"
SRC_URI[sha256sum] = "8b4ac0760018c77c0044fab06a4f0c510ba87eae934d9983b10878483bde7ef7"

UPSTREAM_CHECK_URI = "https://github.com/Mikachu/openbox/tags"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

inherit autotools gettext update-alternatives pkgconfig features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

ALTERNATIVE:${PN}-core = "x-window-manager x-session-manager"
ALTERNATIVE_TARGET[x-window-manager] = "${bindir}/openbox"
ALTERNATIVE_PRIORITY[x-window-manager] = "10"
ALTERNATIVE_TARGET[x-session-manager] = "${bindir}/openbox-session"
ALTERNATIVE_PRIORITY[x-session-manager] = "100"

PACKAGECONFIG ??= ""
PACKAGECONFIG[imlib2] = "--enable-imlib2,--disable-imlib2,imlib2"
PACKAGECONFIG[startup-notification] = "--enable-startup-notification,--disable-startup-notification,startup-notification"
PACKAGECONFIG[xrandr] = "--enable-xrandr,--disable-xrandr,libxrandr"
PACKAGECONFIG[xinerama] = "--enable-xinerama,--disable-xinerama,libxinerama"
PACKAGECONFIG[xcursor] = "--enable-xcursor,--disable-xcursor,libxcursor"

PACKAGES =+ "${PN}-core ${PN}-lxde ${PN}-gnome ${PN}-config"

PACKAGES_DYNAMIC += "^${PN}-theme-.*"

python populate_packages:prepend() {
    theme_dir = d.expand('${datadir}/themes/')
    theme_name = d.expand('${PN}-theme-%s')
    do_split_packages(d, theme_dir, '(.*)', theme_name, '${PN} theme for %s', extra_depends='', allow_dirs=True)
}

FILES:${PN}-core = "${bindir}/openbox ${bindir}/openbox-session ${libdir}/*${SOLIBS}"

FILES:${PN}-lxde += "${datadir}/lxde/ \
                     ${datadir}/lxpanel \
                     ${datadir}/xsessions \
                     ${datadir}/icons"

FILES:${PN}-gnome += " \
    ${bindir}/openbox-gnome-session \
    ${datadir}/gnome \
    ${datadir}/gnome-session \
"

FILES:${PN}-config += "${sysconfdir}"

RDEPENDS:${PN} += "${PN}-core ${PN}-config ${PN}-theme-clearlooks python3 python3-shell pyxdg"
