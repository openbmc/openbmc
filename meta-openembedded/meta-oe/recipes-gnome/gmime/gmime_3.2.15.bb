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
SRC_URI[sha256sum] = "84cd2a481a27970ec39b5c95f72db026722904a2ccf3fdbd57b280cf2d02b5c4"

UPSTREAM_CHECK_URI = "https://github.com/jstedfast/gmime/releases"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

EXTRA_OECONF += "--enable-largefile"

export ac_cv_have_iconv_detect_h="yes"
export ac_cv_sys_file_offset_bits="64"

do_configure:append () {
    cp ${UNPACKDIR}/iconv-detect.h ${S}
}
