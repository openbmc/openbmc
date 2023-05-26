SUMMARY = "Python GObject bindings"
HOMEPAGE = "https://gitlab.gnome.org/GNOME/pygobject"
DESCRIPTION = "PyGObject is a Python package which provides bindings for GObject based libraries such as GTK, GStreamer, WebKitGTK, GLib, GIO and many more."
SECTION = "devel/python"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=a916467b91076e631dd8edb7424769c7"

GNOMEBASEBUILDCLASS = "meson"
GIR_MESON_OPTION = ""

inherit gnomebase setuptools3-base gobject-introspection upstream-version-is-even features_check

REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"

DEPENDS += "python3 glib-2.0"

SRCNAME="pygobject"

SRC_URI = " \
    http://ftp.gnome.org/pub/GNOME/sources/${SRCNAME}/${@gnome_verdir("${PV}")}/${SRCNAME}-${PV}.tar.xz \
    file://0001-Do-not-build-tests.patch \
"
SRC_URI[sha256sum] = "3c6805d1321be90cc32e648215a562430e0d3d6edcda8f4c5e7a9daffcad5710"

S = "${WORKDIR}/${SRCNAME}-${PV}"

PACKAGECONFIG ??= "${@bb.utils.contains_any('DISTRO_FEATURES', [ 'directfb', 'wayland', 'x11' ], 'cairo', '', d)}"

RDEPENDS:${PN} += " \
    python3-io \
    python3-pkgutil \
"

# python3-pycairo is checked on configuration -> DEPENDS
# we don't link against python3-pycairo -> RDEPENDS
PACKAGECONFIG[cairo] = "-Dpycairo=enabled,-Dpycairo=disabled, cairo python3-pycairo, python3-pycairo"

BBCLASSEXTEND = "native"
PACKAGECONFIG:class-native = ""
