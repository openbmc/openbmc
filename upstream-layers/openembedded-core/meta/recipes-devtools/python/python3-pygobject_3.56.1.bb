SUMMARY = "Python GObject bindings"
HOMEPAGE = "https://gitlab.gnome.org/GNOME/pygobject"
DESCRIPTION = "PyGObject is a Python package which provides bindings for GObject based libraries such as GTK, GStreamer, WebKitGTK, GLib, GIO and many more."
SECTION = "devel/python"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=91db794c3adc9d824d4eddabb341e816"

GIR_MESON_OPTION = ""

inherit gnomebase setuptools3-base gobject-introspection upstream-version-is-even

python() {
    if d.getVar('CLASSOVERRIDE') == "class-target" and not bb.utils.to_boolean(d.getVar("GI_DATA_ENABLED")):
        raise bb.parse.SkipRecipe("GI not available")
}

DEPENDS += "python3 glib-2.0"

GNOME_COMPRESS_TYPE = "gz"
GNOMEBN = "pygobject"

SRC_URI[archive.sha256sum] = "2ec1cc8c55c7ffeebb97e58a9bba7aa1e74611f1173628084685446804a8881a"

S = "${UNPACKDIR}/${GNOMEBN}-${PV}"

PACKAGECONFIG ??= "${@bb.utils.contains_any('DISTRO_FEATURES', [ 'directfb', 'wayland', 'x11' ], 'cairo', '', d)}"

RDEPENDS:${PN} += " \
    python3-asyncio \
    python3-io \
    python3-pkgutil \
    gobject-introspection \
"

# python3-pycairo is checked on configuration -> DEPENDS
# we don't link against python3-pycairo -> RDEPENDS
PACKAGECONFIG[cairo] = "-Dpycairo=enabled,-Dpycairo=disabled, cairo python3-pycairo, python3-pycairo"
PACKAGECONFIG[tests] = "-Dtests=true,-Dtests=false,"

BBCLASSEXTEND = "native"
PACKAGECONFIG:class-native = ""
