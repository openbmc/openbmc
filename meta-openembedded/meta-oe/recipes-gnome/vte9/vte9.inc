SUMMARY = "Virtual terminal emulator GTK+ widget library"
BUGTRACKER = "https://bugzilla.gnome.org/buglist.cgi?product=vte"
LICENSE = "LGPL-2.0-only"
DEPENDS = " glib-2.0 gtk+ intltool-native gnome-common-native ncurses"
RDEPENDS:libvte = "vte-termcap"

# help gnomebase get the SRC_URI correct
GNOMEBN = "vte"
S = "${WORKDIR}/vte-${PV}"

inherit gnomebase gtk-doc features_check upstream-version-is-even gobject-introspection
ANY_OF_DISTRO_FEATURES = "${GTK2DISTROFEATURES}"

EXTRA_OECONF = "--disable-python"

PACKAGES =+ "libvte9 vte9-termcap"
FILES:libvte9 = "${libdir}/*.so.* ${libexecdir}/gnome-pty-helper"
FILES:vte9-termcap = "${datadir}/vte/termcap-0.0"
