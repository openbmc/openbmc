SUMMARY = "Glade - A User Interface Designer"
HOMEPAGE = "http://www.gnu.org/software/gnash"
LICENSE = "GPL-2.0-only & LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=aabe87591cb8ae0f3c68be6977bb5522 \
                    file://COPYING.GPL;md5=9ac2e7cff1ddaf48b6eab6028f23ef88 \
                    file://COPYING.LGPL;md5=252890d9eee26aab7b432e8b8a616475"
DEPENDS = "gtk+3 glib-2.0 libxml2 intltool-native \
           gnome-common-native \
           autoconf-archive-native \
"
GNOMEBASEBUILDCLASS = "autotools"
inherit features_check autotools pkgconfig gnomebase gobject-introspection mime-xdg gtk-doc

# xfce4 depends on libwnck3, gtk+3 and libepoxy need to be built with x11 PACKAGECONFIG.
# cairo would at least needed to be built with xlib.
ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI = "http://ftp.gnome.org/pub/GNOME/sources/glade/3.22/glade-${PV}.tar.xz \
           file://remove-yelp-help-rules-var.patch \
          "
SRC_URI[md5sum] = "c074fa378c8f1ad80d20133c4ae6f42d"
SRC_URI[sha256sum] = "edefa6eb24b4d15bd52589121dc109bc08c286157c41288deb74dd9cc3f26a21"

EXTRA_OECONF += "--disable-man-pages"

FILES:${PN} += "${datadir}/glade ${datadir}/metainfo ${libdir}/glade/modules/libgladegtk.so"

# http://errors.yoctoproject.org/Errors/Details/766882/
# glade-3.22.2/gladeui/glade-widget.c:1822:36: error: assignment to 'GtkWidget *' {aka 'struct _GtkWidget *'} from incompatible pointer type 'GObject *' {aka 'struct _GObject *'} [-Wincompatible-pointer-types]
# glade-3.22.2/gladeui/glade-command.c:920:23: error: assignment to 'GladeProperty *' {aka 'struct _GladeProperty *'} from incompatible pointer type 'GObject *' {aka 'struct _GObject *'} [-Wincompatible-pointer-types]
# glade-3.22.2/gladeui/glade-command.c:1253:21: error: assignment to 'GladeWidget *' {aka 'struct _GladeWidget *'} from incompatible pointer type 'GObject *' {aka 'struct _GObject *'} [-Wincompatible-pointer-types]
# glade-3.22.2/gladeui/glade-command.c:1419:21: error: assignment to 'GladeWidget *' {aka 'struct _GladeWidget *'} from incompatible pointer type 'GObject *' {aka 'struct _GObject *'} [-Wincompatible-pointer-types]
CFLAGS += "-Wno-error=incompatible-pointer-types"
