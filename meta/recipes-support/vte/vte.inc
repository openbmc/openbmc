SUMMARY = "Virtual terminal emulator GTK+ widget library"
BUGTRACKER = "https://bugzilla.gnome.org/buglist.cgi?product=vte"
LICENSE = "LGPLv2.0"
DEPENDS = " glib-2.0 gtk+ intltool-native ncurses gobject-introspection-stub"
RDEPENDS_libvte = "vte-termcap"

inherit gnome gtk-doc distro_features_check
ANY_OF_DISTRO_FEATURES = "${GTK2DISTROFEATURES}"

EXTRA_OECONF = "--disable-python --disable-introspection"

PACKAGES =+ "libvte vte-termcap"
FILES_libvte = "${libdir}/*.so.* ${libexecdir}/gnome-pty-helper"
FILES_${PN}-dbg =+ "${libexecdir}/.debug ${prefix}/src ${bindir}/.debug \
               ${libdir}/.debug"
FILES_vte-termcap = "${datadir}/vte/termcap-0.0"

