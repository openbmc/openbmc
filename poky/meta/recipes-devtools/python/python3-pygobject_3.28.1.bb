SUMMARY = "Python GObject bindings"
SECTION = "devel/python"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=a916467b91076e631dd8edb7424769c7"

inherit gnomebase distutils3-base gobject-introspection upstream-version-is-even

DEPENDS += "python3 glib-2.0"

SRCNAME="pygobject"
SRC_URI = " \
    http://ftp.gnome.org/pub/GNOME/sources/${SRCNAME}/${@gnome_verdir("${PV}")}/${SRCNAME}-${PV}.tar.xz \
"

SRC_URI[md5sum] = "612e9e2863d117d810e78672f7bc3ad6"
SRC_URI[sha256sum] = "42312b4a5015571fa0a4f2d201005da46b71c251ea2625bc95702d071c4ff895"

S = "${WORKDIR}/${SRCNAME}-${PV}"

PACKAGECONFIG ??= "${@bb.utils.contains_any('DISTRO_FEATURES', [ 'directfb', 'wayland', 'x11' ], 'cairo', '', d)}"

# python3-pycairo is checked on configuration -> DEPENDS
# we don't link against python3-pycairo -> RDEPENDS
PACKAGECONFIG[cairo] = "--enable-cairo,--disable-cairo,cairo python3-pycairo, python3-pycairo"

RDEPENDS_${PN} += "python3-setuptools"

BBCLASSEXTEND = "native"
PACKAGECONFIG_class-native = ""
