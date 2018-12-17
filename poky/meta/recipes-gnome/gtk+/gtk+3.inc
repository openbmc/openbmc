SUMMARY = "Multi-platform toolkit for creating GUIs"
DESCRIPTION = "GTK+ is a multi-platform toolkit for creating graphical user interfaces. Offering a complete \
set of widgets, GTK+ is suitable for projects ranging from small one-off projects to complete application suites."
HOMEPAGE = "http://www.gtk.org"
BUGTRACKER = "https://bugzilla.gnome.org/"
SECTION = "libs"

DEPENDS = "glib-2.0 cairo pango atk jpeg libpng gdk-pixbuf \
           gdk-pixbuf-native"

LICENSE = "LGPLv2 & LGPLv2+ & LGPLv2.1+"

inherit autotools gettext pkgconfig gtk-doc update-alternatives gtk-immodules-cache gsettings distro_features_check gobject-introspection

# versions >= 3.90 are development versions, otherwise like upstream-version-is-even
UPSTREAM_CHECK_REGEX = "[^\d\.](?P<pver>3\.([1-8]?[02468])+(\.\d+)+)\.tar"

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

# This should be in autotools.bbclass, but until something elses uses it putting
# it here avoids rebuilding everything.
export PKG_CONFIG_FOR_BUILD = "${STAGING_BINDIR_NATIVE}/pkg-config-native"

do_configure_prepend() {
    # Do this because the configure script is running ./libtool directly
    rm -f libtool
    ln -s ${TARGET_PREFIX}libtool libtool
    #delete a file that will get confused with generated one in ${B}
    rm -f ${S}/gtk/gtktypefuncs.c
}

EXTRA_OECONF += " \
                 --disable-glibtest \
                 --disable-xinerama \
                 --enable-modules \
                 --disable-cups \
                 --disable-colord \
                 ${@bb.utils.contains("DISTRO_FEATURES", "x11", "", "--disable-gtk-doc", d)} \
                 "

do_compile_prepend() {
        export GIR_EXTRA_LIBS_PATH="${B}/gdk/.libs"
}

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'opengl wayland x11', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'opengl x11', 'glx', '', d)}"

PACKAGECONFIG[x11] = "--enable-x11-backend,--disable-x11-backend,at-spi2-atk fontconfig libx11 libxext libxcursor libxi libxdamage libxrandr libxrender libxcomposite libxfixes"
# this is provided by oe-core patch that removes epoxy/gl dependency from a X11 build
PACKAGECONFIG[opengl] = "--enable-opengl,--disable-opengl,libepoxy"
PACKAGECONFIG[glx] = "--enable-glx,--disable-glx,,libgl"
PACKAGECONFIG[wayland] = "--enable-wayland-backend,--disable-wayland-backend,wayland wayland-protocols libxkbcommon virtual/mesa wayland-native"

do_install_append() {
	mv ${D}${bindir}/gtk-update-icon-cache ${D}${bindir}/gtk-update-icon-cache-3.0

    # duplicate gtk-query-immodules for post install script update_gtk_immodules_cache
    mkdir -p ${D}${libexecdir}
    ln ${D}${bindir}/gtk-query-immodules-3.0 ${D}${libexecdir}/${MLPREFIX}gtk-query-immodules-3.0
}

PACKAGES =+ "${PN}-demo"
LIBV = "3.0.0"

FILES_${PN}-demo = "${bindir}/gtk3-demo \
                    ${bindir}/gtk3-demo-application \
                    ${bindir}/gtk3-icon-browser \
                    ${bindir}/gtk3-widget-factory \
                    ${datadir}/gtk-3.0/demo \
                    ${datadir}/applications/gtk3-demo.desktop \
                    ${datadir}/applications/gtk3-icon-browser.desktop \
                    ${datadir}/applications/gtk3-widget-factory.desktop \
                    ${datadir}/icons/hicolor/*/apps/gtk3-demo*.png \
                    ${datadir}/icons/hicolor/*/apps/gtk3-widget-factory*.png"

FILES_${PN}_append = " ${bindir}/gtk-update-icon-cache-3.0 \
               ${bindir}/gtk-query-immodules-3.0 \
               ${bindir}/gtk-launch \
               ${datadir}/themes ${sysconfdir} ${datadir}/glib-2.0/schemas/ \
               ${libdir}/gtk-3.0/${LIBV}/engines/libpixmap.so \
               ${libdir}/gtk-3.0/modules/*.so"

FILES_${PN}-dev += " \
                    ${datadir}/gtk-3.0/gtkbuilder.rng \
                    ${datadir}/gtk-3.0/include \
                    ${datadir}/gettext/its \
                    ${libdir}/gtk-3.0/include \
                    ${libdir}/gtk-3.0/${LIBV}/loaders/*.la \
                    ${libdir}/gtk-3.0/${LIBV}/immodules/*.la \
                    ${libdir}/gtk-3.0/3.0.0/printbackends/*.la \
                    ${libdir}/gtk-3.0/${LIBV}/engines/*.la \
                    ${libdir}/gtk-3.0/modules/*.la \
                    ${bindir}/gtk-builder-convert \
                    ${bindir}/gtk-encode-symbolic-svg \
                    ${bindir}/gtk-builder-tool \
                    ${bindir}/gtk-query-settings \
                    "

GTKBASE_RRECOMMENDS ?= "liberation-fonts \
                        gdk-pixbuf-loader-png \
                        gdk-pixbuf-loader-jpeg \
                        gdk-pixbuf-loader-gif \
                        gdk-pixbuf-loader-xpm \
                        shared-mime-info \
                        adwaita-icon-theme-symbolic \
                        "
GTKGLIBC_RRECOMMENDS ?= "${GTKBASE_RRECOMMENDS} glibc-gconv-iso8859-1"

RRECOMMENDS_${PN} = "${GTKBASE_RRECOMMENDS}"
RRECOMMENDS_${PN}_libc-glibc = "${GTKGLIBC_RRECOMMENDS}"
RDEPENDS_${PN}-dev += "${@bb.utils.contains("PACKAGECONFIG", "wayland", "wayland-protocols", "", d)}"

PACKAGES_DYNAMIC += "^gtk3-immodule-.* ^gtk3-printbackend-.*"

ALTERNATIVE_${PN} = "gtk-update-icon-cache"
ALTERNATIVE_TARGET[gtk-update-icon-cache] = "${bindir}/gtk-update-icon-cache-3.0"
ALTERNATIVE_PRIORITY = "30"

python populate_packages_prepend () {
    import os.path

    gtk_libdir = d.expand('${libdir}/gtk-3.0/${LIBV}')
    immodules_root = os.path.join(gtk_libdir, 'immodules')
    printmodules_root = os.path.join(gtk_libdir, 'printbackends');

    immodules = do_split_packages(d, immodules_root, '^im-(.*)\.so$', 'gtk3-immodule-%s', 'GTK input module for %s')
    if immodules:
        d.setVar("GTKIMMODULES_PACKAGES", " ".join(immodules))

    do_split_packages(d, printmodules_root, '^libprintbackend-(.*)\.so$', 'gtk3-printbackend-%s', 'GTK printbackend module for %s')

    if (d.getVar('DEBIAN_NAMES')):
        d.setVar(d.expand('PKG_${PN}'), '${MLPREFIX}libgtk-3.0')
}
