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
    file://0001-python-pyobject-fix-install-dir.patch \
"

SRC_URI[md5sum] = "ca1dc4f31c1d6d283758e8f315a88ab6"
SRC_URI[sha256sum] = "87e2c9aa785f352ef111dcc5f63df9b85cf6e05e52ff04f803ffbebdacf5271a"

S = "${WORKDIR}/${SRCNAME}-${PV}"

UNKNOWN_CONFIGURE_WHITELIST = "introspection"

PACKAGECONFIG ??= "stagedir"

PACKAGECONFIG[cairo] = "-Dpycairo=true,-Dpycairo=false, cairo python-pycairo, python-pycairo"
PACKAGECONFIG[tests] = "-Dtests=true, -Dtests=false, , "
PACKAGECONFIG[python] = "-Dpython=python3, -Dpython=python2, , "
PACKAGECONFIG[stagedir] = "-Dstagedir=${PYTHON_SITEPACKAGES_DIR}, -Dstagedir="", , "

BBCLASSEXTEND = "native"
RDEPENDS_${PN} = "python-pkgutil"
RDEPENDS_${PN}_class-native = ""

do_install_append() {
    # Remove files that clash with python3-pygobject; their content is same
    rm -r ${D}${includedir}/pygobject-3.0/pygobject.h ${D}${libdir}/pkgconfig
}
