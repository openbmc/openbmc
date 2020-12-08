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
SRC_URI[md5sum] = "ebfebc4533856572281add29f08412bf"
SRC_URI[sha256sum] = "d1bf42802d1cec113b5adaa0e7bf7f3745b44521dc2163588d276d5cd61d718f"

UNKNOWN_CONFIGURE_WHITELIST = "introspection"

S = "${WORKDIR}/${SRCNAME}-${PV}"

PACKAGECONFIG ??= "${@bb.utils.contains_any('DISTRO_FEATURES', [ 'directfb', 'wayland', 'x11' ], 'cairo', '', d)}"

RDEPENDS_${PN} += "python3-pkgutil"

# python3-pycairo is checked on configuration -> DEPENDS
# we don't link against python3-pycairo -> RDEPENDS
PACKAGECONFIG[cairo] = "-Dpycairo=true,-Dpycairo=false, cairo python3-pycairo, python3-pycairo"

BBCLASSEXTEND = "native"
PACKAGECONFIG_class-native = ""
