SUMMARY = "A GObject-based Exiv2 wrapper"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=625f055f41728f84a8d7938acc35bdc2"

DEPENDS = "exiv2 python3-pygobject-native"

GTKDOC_MESON_OPTION = "gtk_doc"

inherit gnomebase gobject-introspection gtk-doc python3native vala

SRC_URI[archive.sha256sum] = "2a0c9cf48fbe8b3435008866ffd40b8eddb0667d2212b42396fdf688e93ce0be"

EXTRA_OEMESON = " \
    ${@bb.utils.contains('GI_DATA_ENABLED', 'True', '-Dvapi=true', '-Dvapi=false', d)} \
"

PACKAGES =+ "${PN}-python3"
FILES:${PN}-python3 = "${PYTHON_SITEPACKAGES_DIR}"
RDEPENDS:${PN}-python3 = "${PN}"

PACKAGE_PREPROCESS_FUNCS += "src_package_preprocess"
src_package_preprocess () {
        # Trim build paths from comments in generated sources to ensure reproducibility
        sed -i -e "s,${B}/../${BPN}-${PV}/${BPN}/,,g" \
            ${B}/gexiv2/gexiv2-enums.cpp
}

do_install:append() {
        # gexiv2 harcodes usr/lib as install path, so this corrects it to actual libdir
        if [ "${prefix}/lib" != "${libdir}" ]; then
            mv ${D}/${prefix}/lib/* ${D}/${libdir}/
            rm -rf ${D}/${prefix}/lib
        fi
}
