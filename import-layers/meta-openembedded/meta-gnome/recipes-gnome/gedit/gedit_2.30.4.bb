SUMMARY = "GNOME editor"
SECTION = "x11/gnome"
LICENSE = "GPLv2+"
PR = "r2"

DEPENDS = "gvfs enchant gconf gnome-doc-utils glib-2.0 gtk+ gtksourceview2 iso-codes intltool-native gnome-common-native libice"

LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit gnome gettext pythonnative
SRC_URI = "${GNOME_MIRROR}/${GNOMEBN}/${@gnome_verdir("${PV}")}/${GNOMEBN}-${PV}.tar.${GNOME_COMPRESS_TYPE};name=archive \
           file://0001-workaround-void-pointer-arithmetic.patch \
           file://0001-Remove-help-directory-from-build.patch \
           file://0002-suppress-string-format-literal-warning-to-fix-build-.patch \
           file://0001-tests-document-saver.c-Define-ACCESSPERMS-if-not-def.patch \
           file://0001-gedit-utils-qualify-handle_builder_error-with-format.patch \
           "
SRC_URI[archive.md5sum] = "e1eecb0a92a1a363b3d375ec5ac0fb3b"
SRC_URI[archive.sha256sum] = "a561fe3dd1d199baede1bd07c4ee65f06fc7c494dd4d3327117f04149a608e3c"
GNOME_COMPRESS_TYPE="bz2"

EXTRA_OECONF = "--disable-scrollkeeper \
                --enable-gvfs-metadata"

LDFLAGS += "-lgmodule-2.0 -lICE"

FILES_${PN} += "${libdir}/gedit-2/plugin* ${datadir}/gedit-2"
FILES_${PN}-dbg += "${libdir}/gedit-2/plugin-loaders/.debug ${libdir}/gedit-2/plugins/.debug"
