LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"
DESCRIPTION = "Runtime libraries for parsing and creating MIME mail"
SECTION = "libs"
DEPENDS = "glib-2.0 zlib"

inherit gnomebase gobject-introspection

SRC_URI += "file://iconv-detect.h \
            file://nodolt.patch"

SRC_URI[archive.md5sum] = "656548dc431004d1ebf95e5d0bb4b9c8"
SRC_URI[archive.sha256sum] = "abff194c7c4802fba2e233890d09dde8bf7170c3ad5e13000601c8d5b3c44717"

export ac_cv_have_iconv_detect_h="yes"
do_configure_append = "cp ${WORKDIR}/iconv-detect.h ${S}"
