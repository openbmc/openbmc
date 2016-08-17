SUMMARY = "GNOME editor"
SECTION = "x11/gnome"
LICENSE = "GPLv2+"
PR = "r2"

DEPENDS = "gvfs enchant gconf gnome-doc-utils gnome-doc-utils-native glib-2.0 gtk+ gtksourceview2 iso-codes"

LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit gnome gettext pythonnative
SRC_URI+= "file://0001-workaround-void-pointer-arithmetic.patch"
SRC_URI[archive.md5sum] = "e1eecb0a92a1a363b3d375ec5ac0fb3b"
SRC_URI[archive.sha256sum] = "a561fe3dd1d199baede1bd07c4ee65f06fc7c494dd4d3327117f04149a608e3c"
GNOME_COMPRESS_TYPE="bz2"

EXTRA_OECONF = "--disable-scrollkeeper \
                --enable-gvfs-metadata"

do_configure_prepend() {
    cd ${S}
    gnome-doc-common --copy || true
    gnome-doc-prepare --force --copy || true
    cd ${B}
}

FILES_${PN} += "${libdir}/gedit-2/plugin* ${datadir}/gedit-2"
FILES_${PN}-dbg += "${libdir}/gedit-2/plugin-loaders/.debug ${libdir}/gedit-2/plugins/.debug"

