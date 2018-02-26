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
    file://0001-configure.ac-Don-t-use-gnome-common-macros.patch \
"

SRC_URI[md5sum] = "69a843311d0f0385dff376e11a2d83d2"
SRC_URI[sha256sum] = "a628a95aa0909e13fb08230b1b98fc48adef10b220932f76d62f6821b3fdbffd"

S = "${WORKDIR}/${SRCNAME}-${PV}"


PACKAGECONFIG ??= "${@bb.utils.contains_any('DISTRO_FEATURES', [ 'directfb', 'wayland', 'x11' ], 'cairo', '', d)}"

# python3-pycairo is checked on configuration -> DEPENDS
# we don't link against python3-pycairo -> RDEPENDS
PACKAGECONFIG[cairo] = "--enable-cairo,--disable-cairo,cairo python3-pycairo, python3-pycairo"

RDEPENDS_${PN} += "python3-setuptools python3-importlib"

BBCLASSEXTEND = "native"
PACKAGECONFIG_class-native = ""
