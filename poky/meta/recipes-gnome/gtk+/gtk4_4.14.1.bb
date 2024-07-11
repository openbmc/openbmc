SUMMARY = "Multi-platform toolkit for creating GUIs"
DESCRIPTION = "GTK is a multi-platform toolkit for creating graphical user interfaces. Offering a complete \
set of widgets, GTK is suitable for projects ranging from small one-off projects to complete application suites."
HOMEPAGE = "http://www.gtk.org"
BUGTRACKER = "https://bugzilla.gnome.org/"
SECTION = "libs"

DEPENDS = " \
    atk \
    cairo \
    fribidi \
    gdk-pixbuf \
    gdk-pixbuf-native \
    gi-docgen \
    glib-2.0 \
    graphene \
    harfbuzz \
    jpeg \
    libepoxy \
    libpng \
    librsvg \
    libxkbcommon \
    pango \
    tiff \
"

LICENSE = "LGPL-2.0-only & LGPL-2.0-or-later & LGPL-2.1-or-later"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2 \
    file://gtk/gtk.h;endline=25;md5=61900d77e8d5bc67cf15ad93de9a3490 \
    file://gdk/gdk.h;endline=25;md5=a0fb26c1f6b94e66d148279e192c333f \
    file://tests/testgtk.c;endline=25;md5=49d06770681b8322466b52ed19d29fb2 \
"

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

UPSTREAM_CHECK_REGEX = "gtk-(?P<pver>\d+\.(\d*[02468])+(\.\d+)+)\.tar.xz"

SRC_URI = "http://ftp.gnome.org/pub/gnome/sources/gtk/${MAJ_VER}/gtk-${PV}.tar.xz"
SRC_URI[sha256sum] = "fcefb3f132f8cc4711a9efa5b353c9ae9bb5eeff0246fa74dbc2f2f839b9e308"

S = "${WORKDIR}/gtk-${PV}"

CVE_PRODUCT = "gnome:gtk"

inherit meson gettext pkgconfig gi-docgen update-alternatives gsettings features_check gobject-introspection

# TBD: nativesdk
# gobject-introspection.bbclass pins introspection off for nativesk. As long as
# we do not remove this wisdom or hack gtk4, it is not possible to build
# nativesdk-gtk4
BBCLASSEXTEND = "native"

GSETTINGS_PACKAGE:class-native = ""

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
REQUIRED_DISTRO_FEATURES = "opengl"
GTKDOC_MESON_OPTION = "documentation"
GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'

EXTRA_OEMESON = " -Dbuild-tests=false -Dbuild-testsuite=false -Dbuild-demos=false"

PACKAGECONFIG ??= "gstreamer ${@bb.utils.filter('DISTRO_FEATURES', 'wayland x11 vulkan', d)}"
PACKAGECONFIG:class-native = "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
PACKAGECONFIG:class-nativesdk = "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"

PACKAGECONFIG[x11] = "-Dx11-backend=true,-Dx11-backend=false,at-spi2-atk fontconfig libx11 libxext libxcursor libxi libxdamage libxrandr libxrender libxcomposite libxfixes xinerama"
PACKAGECONFIG[wayland] = "-Dwayland-backend=true,-Dwayland-backend=false,wayland wayland-protocols virtual/egl virtual/libgles2 wayland-native"
PACKAGECONFIG[cloudproviders] = "-Dcloudproviders=enabled,-Dcloudproviders=disabled,libcloudproviders"
PACKAGECONFIG[cups] = "-Dprint-cups=enabled,-Dprint-cups=disabled,cups,cups gtk4-printbackend-cups"
PACKAGECONFIG[colord] = "-Dcolord=enabled,-Dcolord=disabled,colord"
PACKAGECONFIG[iso-codes] = ",,iso-codes,iso-codes"
# gtk4 wants gstreamer-player-1.0 -> gstreamer1.0-plugins-bad
PACKAGECONFIG[gstreamer] = "-Dmedia-gstreamer=enabled,-Dmedia-gstreamer=disabled,gstreamer1.0-plugins-bad"
PACKAGECONFIG[tracker] = "-Dtracker=enabled,-Dtracker=disabled,tracker,tracker-miners"
PACKAGECONFIG[vulkan] = "-Dvulkan=enabled,-Dvulkan=disabled, vulkan-loader vulkan-headers shaderc-native"

# Disable int-conversion warning as error until [1] is fixed
# [1] https://gitlab.gnome.org/GNOME/gtk/-/issues/6033
CFLAGS:append = " -Wno-error=int-conversion"

LIBV = "4.0.0"

FILES:${PN}:append = " \
    ${datadir}/glib-2.0/schemas/ \
    ${datadir}/gtk-4.0/emoji/ \
    ${datadir}/metainfo/ \
    ${datadir}/icons/hicolor/*/apps/org.gtk.PrintEditor4*.* \
    ${libdir}/gtk-4.0/${LIBV}/media \
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

    gtk_libdir = d.expand('${libdir}/gtk-4.0/${LIBV}')
    printmodules_root = os.path.join(gtk_libdir, 'printbackends');

    do_split_packages(d, printmodules_root, r'^libprintbackend-(.*)\.so$', 'gtk4-printbackend-%s', 'GTK printbackend module for %s')

    if (d.getVar('DEBIAN_NAMES')):
        d.setVar(d.expand('PKG:${PN}'), '${MLPREFIX}libgtk-4.0')
}
