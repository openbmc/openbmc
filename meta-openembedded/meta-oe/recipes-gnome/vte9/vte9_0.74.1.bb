SUMMARY = "Virtual terminal emulator GTK+ widget library"
BUGTRACKER = "https://bugzilla.gnome.org/buglist.cgi?product=vte"
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING.LGPL3;md5=b52f2d57d10c4f7ee67a7eb9615d5d24"

DEPENDS = "glib-2.0-native glib-2.0 gnutls gtk+3 gtk4 intltool-native gnome-common-native ncurses"

# help gnomebase get the SRC_URI correct
GNOMEBN = "vte"
S = "${WORKDIR}/vte-${PV}"

SRC_URI[archive.sha256sum] = "2328c3f1c998350a18e0e513348e9fc581d57ea4e7b89aedf11e0e3c65042b4f"

inherit gnomebase gi-docgen gobject-introspection features_check systemd upstream-version-is-even vala
ANY_OF_DISTRO_FEATURES = "${GTK2DISTROFEATURES}"
GIR_MESON_OPTION = "gir"
GIDOCGEN_MESON_OPTION = "docs"

PACKAGECONFIG ?= "gnutls ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[gnutls] = "-Dgnutls=true,-Dgnutls=false,gnutls"
PACKAGECONFIG[fribidi] = "-Dfribidi=true,-Dfribidi=false,fribidi"
PACKAGECONFIG[systemd] = "-D_systemd=true,-D_systemd=false,"

CFLAGS += "-D_GNU_SOURCE"

PACKAGES =+ "libvte9 vte9-termcap"
FILES:libvte9 = "${libdir}/*.so.* ${libexecdir}/gnome-pty-helper ${datadir}/glade ${systemd_user_unitdir}"
FILES:vte9-termcap = "${datadir}/vte/termcap-0.0"

RDEPENDS:libvte = "vte-termcap"
