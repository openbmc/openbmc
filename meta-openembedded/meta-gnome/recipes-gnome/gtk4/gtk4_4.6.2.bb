SUMMARY = "Multi-platform toolkit for creating GUIs"
DESCRIPTION = "GTK is a multi-platform toolkit for creating graphical user interfaces. Offering a complete \
set of widgets, GTK is suitable for projects ranging from small one-off projects to complete application suites."
HOMEPAGE = "http://www.gtk.org"
BUGTRACKER = "https://bugzilla.gnome.org/"
SECTION = "libs"

DEPENDS = " \
    sassc-native \
    glib-2.0 \
    libepoxy \
    graphene \
    cairo \
    pango \
    atk \
    jpeg \
    libpng \
    librsvg \
    tiff \
    gdk-pixbuf-native gdk-pixbuf \
"

LICENSE = "LGPL-2.0-only & LGPL-2.0-or-later & LGPL-2.1-or-later"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2 \
    file://gtk/gtk.h;endline=25;md5=1d8dc0fccdbfa26287a271dce88af737 \
    file://gdk/gdk.h;endline=25;md5=c920ce39dc88c6f06d3e7c50e08086f2 \
    file://tests/testgtk.c;endline=25;md5=49d06770681b8322466b52ed19d29fb2 \
"

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

UPSTREAM_CHECK_REGEX = "gtk-(?P<pver>\d+\.(\d*[02468])+(\.\d+)+)\.tar.xz"

SRC_URI = "http://ftp.gnome.org/pub/gnome/sources/gtk/${MAJ_VER}/gtk-${PV}.tar.xz"
SRC_URI[sha256sum] = "ff263af609a50eb76056653592d929459aef4819a444c436f6d52c6f63c1faec"

S = "${WORKDIR}/gtk-${PV}"

inherit meson gettext pkgconfig gtk-doc update-alternatives gsettings features_check gobject-introspection

# TBD: nativesdk
# gobject-introspection.bbclass pins introspection off for nativesk. As long as
# we do not remove this wisdom or hack gtk4, it is not possible to build
# nativesdk-gtk4
BBCLASSEXTEND = "native"

GSETTINGS_PACKAGE:class-native = ""

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
REQUIRED_DISTRO_FEATURES = "opengl"

GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'
GTKDOC_MESON_OPTION = 'gtk_doc'

EXTRA_OEMESON = " -Dbuild-tests=false"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'wayland x11', d)}"
PACKAGECONFIG:class-native = "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
PACKAGECONFIG:class-nativesdk = "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"

PACKAGECONFIG[x11] = "-Dx11-backend=true,-Dx11-backend=false,at-spi2-atk fontconfig libx11 libxext libxcursor libxi libxdamage libxrandr libxrender libxcomposite libxfixes xinerama"
PACKAGECONFIG[wayland] = "-Dwayland-backend=true,-Dwayland-backend=false,wayland wayland-protocols libxkbcommon virtual/egl virtual/libgles2 wayland-native"
PACKAGECONFIG[cups] = "-Dprint-cups=enabled,-Dprint-cups=disabled,cups"
PACKAGECONFIG[colord] = "-Dcolord=enabled,-Dcolord=disabled,colord"
# gtk4 wants gstreamer-player-1.0 -> gstreamer1.0-plugins-bad
PACKAGECONFIG[gstreamer] = "-Dmedia-gstreamer=enabled,-Dmedia-gstreamer=disabled,gstreamer1.0-plugins-bad"
PACKAGECONFIG[tracker] = "-Dtracker=enabled,-Dtracker=disabled,tracker"


do_compile:prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/gdk/.libs"
}


PACKAGES =+ "${PN}-demo"
LIBV = "4.0.0"

FILES:${PN}-demo = " \
    ${datadir}/applications/org.gtk.Demo4.desktop \
    ${datadir}/applications/org.gtk.IconBrowser4.desktop \
    ${datadir}/applications/org.gtk.WidgetFactory4.desktop \
    ${datadir}/icons/hicolor/*/apps/org.gtk.Demo4*.* \
    ${datadir}/icons/hicolor/*/apps/org.gtk.IconBrowser4*.* \
    ${datadir}/icons/hicolor/*/apps/org.gtk.WidgetFactory4*.* \
    ${bindir}/gtk4-demo \
    ${bindir}/gtk4-demo-application \
    ${bindir}/gtk4-icon-browser \
    ${bindir}/gtk4-widget-factory \
"

FILES:${PN}:append = " \
    ${datadir}/glib-2.0/schemas/ \
    ${datadir}/gtk-4.0/emoji/ \
    ${datadir}/metainfo/ \
    ${datadir}/icons/hicolor/*/apps/org.gtk.PrintEditor4*.* \
    ${libdir}/gtk-4.0/${LIBV}/printbackends \
    ${bindir}/gtk4-update-icon-cache \
    ${bindir}/gtk4-launch \
"

FILES:${PN}-dev += " \
    ${datadir}/gtk-4.0/gtk4builder.rng \
    ${datadir}/gtk-4.0/include \
    ${datadir}/gtk-4.0/valgrind \
    ${datadir}/gettext/its \
    ${bindir}/gtk4-builder-tool \
    ${bindir}/gtk4-encode-symbolic-svg \
    ${bindir}/gtk4-query-settings \
"

GTKBASE_RRECOMMENDS ?= " \
    liberation-fonts \
    gdk-pixbuf-loader-png \
    gdk-pixbuf-loader-jpeg \
    gdk-pixbuf-loader-gif \
    gdk-pixbuf-loader-xpm \
    shared-mime-info \
    adwaita-icon-theme-symbolic \
"

GTKBASE_RRECOMMENDS:class-native ?= ""

GTKGLIBC_RRECOMMENDS ?= "${GTKBASE_RRECOMMENDS} glibc-gconv-iso8859-1"

RRECOMMENDS:${PN} = "${GTKBASE_RRECOMMENDS}"
RRECOMMENDS:${PN}:libc-glibc = "${GTKGLIBC_RRECOMMENDS}"
RDEPENDS:${PN}-dev += "${@bb.utils.contains("PACKAGECONFIG", "wayland", "wayland-protocols", "", d)}"

PACKAGES_DYNAMIC += "^gtk4-printbackend-.*"
python populate_packages:prepend () {
    import os.path

    gtk_libdir = d.expand('${libdir}/gtk-3.0/${LIBV}')
    printmodules_root = os.path.join(gtk_libdir, 'printbackends');

    do_split_packages(d, printmodules_root, r'^libprintbackend-(.*)\.so$', 'gtk4-printbackend-%s', 'GTK printbackend module for %s')

    if (d.getVar('DEBIAN_NAMES')):
        d.setVar(d.expand('PKG:${PN}'), '${MLPREFIX}libgtk-4.0')
}
