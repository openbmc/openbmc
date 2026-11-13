SUMMARY = "Intelligent Input Bus for Linux/Unix"
HOMEPAGE = "https://github.com/ibus/ibus/wiki"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

DEPENDS = "unicode-ucd libx11-native glib-2.0-native glib-2.0 dbus iso-codes"

SRC_URI = " \
    git://github.com/ibus/ibus.git;branch=main;protocol=https;tag=${PV} \
    file://0001-Do-not-try-to-start-dbus-we-do-not-have-dbus-lauch.patch \
    file://0001-src-make-an-IBusText-own-an-updated-IBusAttrList-ref.patch \
    file://0002-src-Fix-IBusAttrList-leak-when-converting-text.patch \
"
SRCREV = "1f7af28437afd62a6d145bfc81035e698a37411d"

# brokensep needed as vapi fails to build correctly
inherit autotools-brokensep gettext features_check pkgconfig
inherit bash-completion gobject-introspection gtk-doc gtk-icon-cache vala

PACKAGECONFIG ??= " \
    ${@bb.utils.filter('DISTRO_FEATURES', 'systemd wayland x11', d)} \
    ${@bb.utils.contains_any('DISTRO_FEATURES', [ 'wayland', 'x11' ], 'gtk3 gtk4', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'libnotify', '', d)} \
    dconf vala \
"
PACKAGECONFIG[appindicator] = "--enable-appindicator,--disable-appindicator,qtbase"
PACKAGECONFIG[dconf] = "--enable-dconf,--disable-dconf,dconf"
PACKAGECONFIG[gtk2] = "--enable-gtk2,--disable-gtk2,gtk+"
PACKAGECONFIG[gtk3] = "--enable-gtk3,--disable-gtk3,gtk+3"
PACKAGECONFIG[gtk4] = "--enable-gtk4,--disable-gtk4,gtk4"
PACKAGECONFIG[libnotify] = "--enable-libnotify,--disable-libnotify,libnotify"
PACKAGECONFIG[systemd] = "--enable-systemd-services,--disable-systemd-services,systemd"
PACKAGECONFIG[wayland]  = "--enable-wayland,--disable-wayland,wayland"
PACKAGECONFIG[vala]  = "--enable-vala,--disable-vala"
PACKAGECONFIG[x11]  = "--enable-xim --enable-ui,--disable-xim --disable-ui,virtual/libx11"

EXTRA_OECONF = " \
    --disable-tests \
    --disable-emoji-dict \
    --disable-python2 \
    --with-python=${bindir}/python3 \
    --with-ucd-dir=${STAGING_DATADIR}/unicode/ucd \
"

do_configure:prepend() {
    touch ${S}/ChangeLog ${S}/ABOUT-NLS
    # Remove vapigen.m4 bundled with sources so that the one shipped by vala is used instead.
    rm -f ${S}/m4/vapigen.m4
}

do_compile:prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/src/.libs"
    cp ${STAGING_DATADIR_NATIVE}/X11/locale/en_US.UTF-8/Compose ${B}/src/Compose
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
