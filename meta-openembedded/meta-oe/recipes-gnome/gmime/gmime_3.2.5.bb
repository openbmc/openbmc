LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"
DESCRIPTION = "Runtime libraries for parsing and creating MIME mail"
SECTION = "libs"
DEPENDS = "glib-2.0 zlib"

inherit gnomebase gobject-introspection

SRC_URI += "file://iconv-detect.h \
            file://nodolt.patch"

SRC_URI[archive.md5sum] = "98970e3995e67ac3f23827ff52308f9e"
SRC_URI[archive.sha256sum] = "fb7556501f85c3bf3e65fdd82697cbc4fa4b55dccd33ad14239ce0197e78ba59"

export ac_cv_have_iconv_detect_h="yes"
do_configure_append = "cp ${WORKDIR}/iconv-detect.h ${S}"
