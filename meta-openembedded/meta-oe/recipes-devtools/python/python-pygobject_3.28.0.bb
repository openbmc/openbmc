SUMMARY = "Python GObject bindings"
HOMEPAGE = "http://www.pygtk.org/"
SECTION = "devel/python"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=a916467b91076e631dd8edb7424769c7"

inherit autotools pkgconfig gnomebase distutils-base gobject-introspection

PYTHON_BASEVERSION = "2.7"
PYTHON_PN = "python"

DEPENDS += "gnome-common-native python glib-2.0"

SRCNAME="pygobject"
SRC_URI = " \
    http://ftp.gnome.org/pub/GNOME/sources/${SRCNAME}/${@gnome_verdir("${PV}")}/${SRCNAME}-${PV}.tar.xz \
    file://0001-configure.ac-add-sysroot-path-to-GI_DATADIR-don-t-se.patch \
"

SRC_URI[md5sum] = "b29d69edb63ae1f555afeb19f90b9996"
SRC_URI[sha256sum] = "42b47b261b45aedfc77e02e3c90a01cd74d6f86c3273c1860a054d531d606e5a"

S = "${WORKDIR}/${SRCNAME}-${PV}"

BBCLASSEXTEND = "native"

EXTRA_OECONF = "--disable-cairo --with-python=${PYTHON}"

RDEPENDS_${PN} += "python-setuptools"
RDEPENDS_${PN}_class-native = ""

do_install_append() {
    # Remove files that clash with python3-pygobject; their content is same
    rm -rf ${D}${includedir}/pygobject-3.0//pygobject.h ${D}${libdir}/pkgconfig
}
