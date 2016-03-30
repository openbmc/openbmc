SUMMARY = "Runtime support for the GTK+ interface builder"
HOMEPAGE = "http://library.gnome.org/devel/libglade/"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "LGPLv2 & LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=55ca817ccb7d5b5b66355690e9abc605 \
                    file://glade/glade.h;endline=22;md5=a04f461c964ba4b57a172d1fbcd8f8fc \
                    file://glade/glade-gtk.c;endline=22;md5=766f993433e2642fec87936d319990ff"

SECTION = "libs"
PR = "r5"
DEPENDS = "zlib gdk-pixbuf gtk+"

inherit autotools pkgconfig gnomebase gtk-doc distro_features_check
ANY_OF_DISTRO_FEATURES = "${GTK2DISTROFEATURES}"
GNOME_COMPRESS_TYPE="bz2"

SRC_URI += "file://glade-cruft.patch file://no-xml2.patch file://python_environment.patch"

SRC_URI[archive.md5sum] = "d1776b40f4e166b5e9c107f1c8fe4139"
SRC_URI[archive.sha256sum] = "64361e7647839d36ed8336d992fd210d3e8139882269bed47dc4674980165dec"

EXTRA_OECONF += "--without-libxml2"

CFLAGS += "-lz"

PACKAGES += " ${PN}-data"
FILES_${PN} = "${libdir}/lib*.so.*"
FILES_${PN}-data = "${datadir}/xml/libglade/glade-2.0.dtd"
FILES_${PN}-dev += "${bindir}/libglade-convert"
#RDEPENDS_${PN} = "${PN}-data"
