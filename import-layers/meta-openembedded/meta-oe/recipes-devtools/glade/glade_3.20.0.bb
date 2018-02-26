SUMMARY = "Glade - A User Interface Designer"
HOMEPAGE = "http://www.gnu.org/software/gnash"
LICENSE = "GPLv2 & LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=aabe87591cb8ae0f3c68be6977bb5522 \
                    file://COPYING.GPL;md5=9ac2e7cff1ddaf48b6eab6028f23ef88 \
                    file://COPYING.LGPL;md5=252890d9eee26aab7b432e8b8a616475"
DEPENDS = "gtk+ gtk+3 glib-2.0 libxml2 intltool-native \
           gnome-common-native \
"


inherit autotools pkgconfig gnomebase gobject-introspection

SRC_URI = "http://ftp.gnome.org/pub/GNOME/sources/glade/3.20/glade-${PV}.tar.xz \
           file://remove-yelp-help-rules-var.patch \
          "
SRC_URI[md5sum] = "9964a2da14c5f845eae363889586ca43"
SRC_URI[sha256sum] = "82d96dca5dec40ee34e2f41d49c13b4ea50da8f32a3a49ca2da802ff14dc18fe"

EXTRA_OECONF += "--disable-man-pages"

FILES_${PN} += "${datadir}/* ${libdir}/glade/modules/libgladegtk.so"
FILES_${PN}-dev += "${libdir}/glade/modules/libgladegtk.la"
FILES_${PN}-dbg += "${libdir}/glade/modules/.debug/libgladegtk.so"

