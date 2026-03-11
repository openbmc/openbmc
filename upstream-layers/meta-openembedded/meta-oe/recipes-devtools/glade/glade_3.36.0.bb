SUMMARY = "Glade - A User Interface Designer"
HOMEPAGE = "https://glade.gnome.org"
LICENSE = "GPL-2.0-only & LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=aabe87591cb8ae0f3c68be6977bb5522 \
                    file://COPYING.GPL;md5=4641e94ec96f98fabc56ff9cc48be14b \
                    file://COPYING.LGPL;md5=81227099add6b483afd7b1d4fc4e93b7"
DEPENDS = "gtk+3 glib-2.0 libxml2 intltool-native itstool-native \
           gnome-common-native \
           autoconf-archive-native \
"
GNOMEBASEBUILDCLASS = "autotools"
inherit features_check autotools gettext pkgconfig gnomebase gobject-introspection mime-xdg gtk-doc

# xfce4 depends on libwnck3, gtk+3 and libepoxy need to be built with x11 PACKAGECONFIG.
# cairo would at least needed to be built with xlib.
ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI = "${GNOME_MIRROR}/glade/3.36/glade-${PV}.tar.xz \
           file://remove-yelp-help-rules-var.patch \
           file://CVE-2020-36774.patch \
          "
SRC_URI[sha256sum] = "19b546b527cc46213ccfc8022d49ec57e618fe2caa9aa51db2d2862233ea6f08"

EXTRA_OECONF += "--disable-man-pages"

FILES:${PN} += "${datadir}/glade ${datadir}/metainfo ${libdir}/glade/modules/libgladegtk.so"







