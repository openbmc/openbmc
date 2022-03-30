LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"
DESCRIPTION = "Runtime libraries for parsing and creating MIME mail"
SECTION = "libs"

DEPENDS = "glib-2.0 zlib"

inherit gnomebase gobject-introspection vala

SRC_URI += "file://iconv-detect.h \
            file://nodolt.patch"
SRC_URI[archive.sha256sum] = "2aea96647a468ba2160a64e17c6dc6afe674ed9ac86070624a3f584c10737d44"

export ac_cv_have_iconv_detect_h="yes"

do_configure:append () {
    cp ${WORKDIR}/iconv-detect.h ${S}
}
