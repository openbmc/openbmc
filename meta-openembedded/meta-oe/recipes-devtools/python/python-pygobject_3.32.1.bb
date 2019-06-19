SUMMARY = "Python GObject bindings"
HOMEPAGE = "http://www.pygtk.org/"
SECTION = "devel/python"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=a916467b91076e631dd8edb7424769c7"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase gobject-introspection distutils-base upstream-version-is-even

DEPENDS += "python glib-2.0"

SRCNAME = "pygobject"
SRC_URI = " \
    http://ftp.gnome.org/pub/GNOME/sources/${SRCNAME}/${@gnome_verdir("${PV}")}/${SRCNAME}-${PV}.tar.xz \
"

SRC_URI[md5sum] = "9d5dbca10162dd9b0d03fed0c6cf865d"
SRC_URI[sha256sum] = "32c99def94b8dea5ce9e4bc99576ef87591ea779b4db77cfdca7af81b76d04d8"

S = "${WORKDIR}/${SRCNAME}-${PV}"

UNKNOWN_CONFIGURE_WHITELIST = "introspection"

PACKAGECONFIG[cairo] = "-Dpycairo=true,-Dpycairo=false, cairo python-pycairo, python-pycairo"
PACKAGECONFIG[tests] = "-Dtests=true, -Dtests=false, , "


BBCLASSEXTEND = "native"
RDEPENDS_${PN} = "python-setuptools"
RDEPENDS_${PN}_class-native = ""

do_install_append() {
    # Remove files that clash with python3-pygobject; their content is same
    rm -r ${D}${includedir}/pygobject-3.0/pygobject.h ${D}${libdir}/pkgconfig
}
