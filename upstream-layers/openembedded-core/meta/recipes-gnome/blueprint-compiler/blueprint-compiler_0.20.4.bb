SUMMARY = "A markup language for GTK user interface files."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/blueprint-compiler"
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=3000208d539ec061b899bce1d9ce9404"

SRC_URI = "git://gitlab.gnome.org/GNOME/blueprint-compiler;protocol=https;branch=blueprint-0-20;tag=v${PV}"
SRCREV = "31b62c24a72c1670d2d93dcdf2d130f1ae12778e"

inherit meson pkgconfig python3-dir gobject-introspection-data

# The compiler needs python3-pygobject on target, which is skipped without
# GI data (which also depends on qemu-usermode), so skip along with it.
python() {
    if d.getVar('CLASSOVERRIDE') == "class-target" and not bb.utils.to_boolean(d.getVar("GI_DATA_ENABLED")):
        raise bb.parse.SkipRecipe("GI not available")
}

PACKAGES += "${PN}-python"

FILES:${PN}-python = "${PYTHON_SITEPACKAGES_DIR}"
RDEPENDS:${PN}-python = "python3-pygobject"

BBCLASSEXTEND = "native"
