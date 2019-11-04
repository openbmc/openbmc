LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"
DESCRIPTION = "Runtime libraries for parsing and creating MIME mail"
SECTION = "libs"
DEPENDS = "glib-2.0 zlib"

inherit gnomebase autotools gobject-introspection

SRC_URI += "file://iconv-detect.h \
            file://nodolt.patch"

SRC_URI[archive.md5sum] = "b6b4e9fdc8f3336551d23872c83b539a"
SRC_URI[archive.sha256sum] = "249ea7c0e080b067aa9669162c36b181b402f6cf6cebc4999d838c6f1e81d024"

EXTRA_OECONF_remove = "--disable-schemas-install"

export ac_cv_have_iconv_detect_h="yes"
do_configure_append = "cp ${WORKDIR}/iconv-detect.h ${S}"

# we do not need GNOME 1 gnome-config support
do_install_append () {
    rm -f ${D}${libdir}/gmimeConf.sh
}
