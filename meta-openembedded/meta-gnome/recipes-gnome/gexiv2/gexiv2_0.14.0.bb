SUMMARY = "A GObject-based Exiv2 wrapper"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=625f055f41728f84a8d7938acc35bdc2"

DEPENDS = "exiv2 python3-pygobject-native"

GNOMEBASEBUILDCLASS = "meson"
GTKDOC_MESON_OPTION = "gtk_doc"

inherit gnomebase gobject-introspection gtk-doc python3native

SRC_URI[archive.sha256sum] = "e58279a6ff20b6f64fa499615da5e9b57cf65ba7850b72fafdf17221a9d6d69e"

EXTRA_OEMESON = " \
    -Dvapi=false \
    -Dpython3_girdir=${PYTHON_SITEPACKAGES_DIR}/gi/overrides \
"

PACKAGES =+ "${PN}-python3"
FILES:${PN}-python3 = "${PYTHON_SITEPACKAGES_DIR}"
RDEPENDS:${PN}-python3 = "${PN}"
