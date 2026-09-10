SUMMARY = "Glade - A User Interface Designer"
HOMEPAGE = "https://glade.gnome.org"
LICENSE = "GPL-2.0-only AND LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=aabe87591cb8ae0f3c68be6977bb5522 \
                    file://COPYING.GPL;md5=4641e94ec96f98fabc56ff9cc48be14b \
                    file://COPYING.LGPL;md5=81227099add6b483afd7b1d4fc4e93b7"
DEPENDS = "gtk+3 glib-2.0 libxml2 intltool-native itstool-native \
           gnome-common-native \
           autoconf-archive-native \
"
inherit features_check gettext pkgconfig gnomebase gobject-introspection mime-xdg gtk-doc

# xfce4 depends on libwnck3, gtk+3 and libepoxy need to be built with x11 PACKAGECONFIG.
# cairo would at least needed to be built with xlib.
ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI += "file://0001-gladeui-enum-types-templates-use-basename-instead-o.patch"
SRC_URI[archive.sha256sum] = "31c9adaea849972ab9517b564e19ac19977ca97758b109edc3167008f53e3d9c"

# Man pages are generated with xsltproc pulling the docbook stylesheets over
# the network, so keep them disabled like the autotools build did.
EXTRA_OEMESON += "-Dman=false"

# 3.40.0 renamed the docs meson option to "gtk_doc" and dropped the
# "introspection" option entirely (introspection is no longer optional), so
# repoint gtk-doc.bbclass at the new name and tell gobject-introspection.bbclass
# not to inject a -Dintrospection flag that no longer exists.
GTKDOC_MESON_OPTION = "gtk_doc"
GIR_MESON_OPTION = ""

PACKAGECONFIG ??= ""
PACKAGECONFIG[gjs] = "-Dgjs=enabled,-Dgjs=disabled,gjs"
PACKAGECONFIG[python] = "-Dpython=enabled,-Dpython=disabled,python3-pygobject"
PACKAGECONFIG[webkit] = "-Dwebkit2gtk=enabled,-Dwebkit2gtk=disabled,webkitgtk3"

FILES:${PN} += "${datadir}/glade ${datadir}/metainfo ${libdir}/glade/modules/libgladegtk.so \
                ${datadir}/gettext ${datadir}/help \
"
