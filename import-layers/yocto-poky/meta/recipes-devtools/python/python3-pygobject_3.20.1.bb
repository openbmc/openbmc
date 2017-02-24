SUMMARY = "Python GObject bindings"
SECTION = "devel/python"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=a916467b91076e631dd8edb7424769c7"

inherit autotools pkgconfig gnomebase distutils3-base gobject-introspection upstream-version-is-even

DEPENDS += "python3 glib-2.0"

SRCNAME="pygobject"
SRC_URI = " \
    http://ftp.gnome.org/pub/GNOME/sources/${SRCNAME}/${@gnome_verdir("${PV}")}/${SRCNAME}-${PV}.tar.xz \
    file://0001-configure.ac-add-sysroot-path-to-GI_DATADIR-don-t-se.patch \
"

SRC_URI[md5sum] = "4354c6283b135f859563b72457f6a321"
SRC_URI[sha256sum] = "3d261005d6fed6a92ac4c25f283792552f7dad865d1b7e0c03c2b84c04dbd745"

S = "${WORKDIR}/${SRCNAME}-${PV}"

BBCLASSEXTEND = "native"

EXTRA_OECONF = "--disable-cairo"

RDEPENDS_${PN} += "python3-setuptools python3-importlib"
