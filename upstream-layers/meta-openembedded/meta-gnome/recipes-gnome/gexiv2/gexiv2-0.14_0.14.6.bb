SUMMARY = "A GObject-based Exiv2 wrapper"
DESCRIPTION = "The 0.14 series of gexiv2 for applications that don't support the \
versioned gexiv2-0.16 API yet, most notably GIMP. It installs in parallel to the \
current gexiv2 recipe: the library, pkg-config module, header directory and GIR \
namespace version differ. The command line tool and the python override are not \
built here because their files would clash with the current version."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=625f055f41728f84a8d7938acc35bdc2"

DEPENDS = "exiv2"

GNOMEBN = "gexiv2"

GTKDOC_MESON_OPTION = "gtk_doc"

inherit gnomebase gobject-introspection gtk-doc vala

SRC_URI[archive.sha256sum] = "606c28aaae7b1f3ef5c8eabe5e7dffd7c5a1c866d25b7671fb847fe287a72b8b"

# The command line tool and the python override would clash with the files
# installed by the current gexiv2 recipe.
# AI-Generated: this recipe was created with Claude Code.
EXTRA_OEMESON = " \
    -Dpython3=false \
    -Dtools=false \
    ${@bb.utils.contains('GI_DATA_ENABLED', 'True', '-Dvapi=true', '-Dvapi=false', d)} \
"

PACKAGE_PREPROCESS_FUNCS += "src_package_preprocess"
src_package_preprocess () {
        # Trim build paths from code in generated sources to ensure reproducibility
        sed -i -e "s,${B}/../sources/${GNOMEBN}-${PV},${TARGET_DBGSRC_DIR},g" \
            ${B}/gexiv2/gexiv2-enums.cpp
}
