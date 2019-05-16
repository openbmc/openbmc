SUMMARY = "Python GObject bindings"
SECTION = "devel/python"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=a916467b91076e631dd8edb7424769c7"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase distutils3-base gobject-introspection upstream-version-is-even

DEPENDS += "python3 glib-2.0"

SRCNAME="pygobject"
SRC_URI = " \
    http://ftp.gnome.org/pub/GNOME/sources/${SRCNAME}/${@gnome_verdir("${PV}")}/${SRCNAME}-${PV}.tar.xz \
    file://0001-Do-not-build-tests.patch \
"

UNKNOWN_CONFIGURE_WHITELIST = "introspection"

SRC_URI[md5sum] = "9d5dbca10162dd9b0d03fed0c6cf865d"
SRC_URI[sha256sum] = "32c99def94b8dea5ce9e4bc99576ef87591ea779b4db77cfdca7af81b76d04d8"

S = "${WORKDIR}/${SRCNAME}-${PV}"

PACKAGECONFIG ??= "${@bb.utils.contains_any('DISTRO_FEATURES', [ 'directfb', 'wayland', 'x11' ], 'cairo', '', d)}"

# python3-pycairo is checked on configuration -> DEPENDS
# we don't link against python3-pycairo -> RDEPENDS
PACKAGECONFIG[cairo] = "-Dpycairo=true,-Dpycairo=false, cairo python3-pycairo, python3-pycairo"

RDEPENDS_${PN} += "python3-setuptools"

BBCLASSEXTEND = "native"
PACKAGECONFIG_class-native = ""
