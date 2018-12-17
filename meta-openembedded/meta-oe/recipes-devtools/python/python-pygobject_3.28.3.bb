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

SRC_URI[md5sum] = "3bac63c86bb963aa401f97859464aa90"
SRC_URI[sha256sum] = "3dd3e21015d06e00482ea665fc1733b77e754a6ab656a5db5d7f7bfaf31ad0b0"

S = "${WORKDIR}/${SRCNAME}-${PV}"

BBCLASSEXTEND = "native"

EXTRA_OECONF = "--disable-cairo --with-python=${PYTHON}"

RDEPENDS_${PN} += "python-setuptools"
RDEPENDS_${PN}_class-native = ""

do_install_append() {
    # Remove files that clash with python3-pygobject; their content is same
    rm -rf ${D}${includedir}/pygobject-3.0//pygobject.h ${D}${libdir}/pkgconfig
}
