DEPENDS_prepend = "virtual/libintl intltool-native "
GPE_TARBALL_SUFFIX ?= "gz"
SRC_URI = "${GPE_MIRROR}/${BP}.tar.${GPE_TARBALL_SUFFIX}"
FILES_${PN} += "${datadir}/gpe ${datadir}/application-registry"
SECTION ?= "gpe"

inherit gettext

gpe_do_compile() {
    oe_runmake PREFIX=${prefix}
}

gpe_do_install() {
    oe_runmake PREFIX=${prefix} DESTDIR=${D} install
}

EXPORT_FUNCTIONS do_compile do_install
