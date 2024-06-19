LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"
DESCRIPTION = "Runtime libraries for parsing and creating MIME mail"
SECTION = "libs"

DEPENDS = "glib-2.0 zlib"

GNOMEBASEBUILDCLASS = "autotools"
inherit gnomebase gobject-introspection vala gtk-doc

SRC_URI = "https://github.com/jstedfast/${BPN}/releases/download/${PV}/${BP}.tar.xz \
    file://iconv-detect.h \
"
SRC_URI[sha256sum] = "2e10a54d4821daf8b16c019ad5d567e0fb8e766f8ffe5fec3d4c6a37373d6406"

EXTRA_OECONF += "--enable-largefile"

export ac_cv_have_iconv_detect_h="yes"
export ac_cv_sys_file_offset_bits="64"

do_configure:append () {
    cp ${UNPACKDIR}/iconv-detect.h ${S}
}
