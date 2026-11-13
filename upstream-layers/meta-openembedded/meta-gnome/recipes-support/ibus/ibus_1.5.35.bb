SUMMARY = "Intelligent Input Bus for Linux/Unix"
HOMEPAGE = "https://github.com/ibus/ibus/wiki"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

DEPENDS = "unicode-ucd libx11 glib-2.0-native glib-2.0 dbus iso-codes"

SRC_URI = " \
    git://github.com/ibus/ibus.git;branch=main;protocol=https;tag=${PV} \
    file://0001-Do-not-try-to-start-dbus-we-do-not-have-dbus-lauch.patch \
    file://0002-meson-define-pyoverridesdir-without-PyGObject.patch \
"
SRCREV = "d464316d9b9613005c5573a010fec7f9b40b93b6"

inherit meson gettext features_check pkgconfig python3native
inherit bash-completion gobject-introspection gtk-doc gtk-icon-cache vala

PACKAGECONFIG ??= " \
    ${@bb.utils.filter('DISTRO_FEATURES', 'systemd wayland x11', d)} \
    ${@bb.utils.contains_any('DISTRO_FEATURES', [ 'wayland', 'x11' ], 'gtk3 gtk4', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'libnotify', '', d)} \
    dconf vala \
"
PACKAGECONFIG[appindicator] = "-Dappindicator=true,-Dappindicator=false,qtbase"
PACKAGECONFIG[dconf] = "-Ddconf=enabled,-Ddconf=disabled,dconf"
PACKAGECONFIG[gtk2] = "-Dgtk2=enabled,-Dgtk2=disabled,gtk+"
PACKAGECONFIG[gtk3] = "-Dgtk3=true,-Dgtk3=false,gtk+3"
PACKAGECONFIG[gtk4] = "-Dgtk4=true,-Dgtk4=false,gtk4"
PACKAGECONFIG[libnotify] = "-Dlibnotify=true,-Dlibnotify=false,libnotify"
PACKAGECONFIG[systemd] = "-Dsystemd-services=enabled,-Dsystemd-services=disabled,systemd"
PACKAGECONFIG[wayland] = "-Dwayland=enabled,-Dwayland=disabled,wayland wayland-native"
PACKAGECONFIG[vala] = "-Dvapi=enabled,-Dvapi=disabled"
PACKAGECONFIG[x11] = "-Dxim=enabled -Dui=true,-Dxim=disabled -Dui=false,virtual/libx11"

GTKDOC_MESON_OPTION = "gtk-doc"

EXTRA_OEMESON = " \
    -Dtests=false \
    -Demoji-dict=false \
    -Ducd-dir=${STAGING_DATADIR}/unicode/ucd \
"

do_install:append() {
    sed -i -e 's|${PYTHON}|${bindir}/python3|' ${D}${bindir}/ibus-setup
}

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/GConf \
    ${datadir}/glib-2.0 \
    ${libdir}/gtk-2.0 \
    ${libdir}/gtk-3.0 \
    ${libdir}/gtk-4.0 \
    ${systemd_user_unitdir} \
"

FILES:${PN}-dev += " \
    ${datadir}/gettext \
"

RDEPENDS:${PN} += "python3-core"

REQUIRED_DISTRO_FEATURES = "${@bb.utils.contains('PACKAGECONFIG', 'gtk4', 'opengl', '', d)}"
