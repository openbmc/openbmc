SUMMARY = "Helper lib for keyboard management"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=6e29c688d912da12b66b73e32b03d812"

DEPENDS = "xkbcomp gtk+ iso-codes libxi libxml2"

GNOME_COMPRESS_TYPE = "xz"

inherit gnomebase gettext gobject-introspection
SRC_URI[archive.md5sum] = "290ea2a8abc40f78a3a16bdae6f02808"
SRC_URI[archive.sha256sum] = "ebec3bc54b5652838502b96223152fb1cd8fcb14ace5cb02d718fc3276bbd404"

EXTRA_OECONF = "--with-xkb-bin-base=${bindir}"

do_configure_append() {
    find ${B} -name Makefile | xargs sed -i s:'-I$(includedir)':'-I.':g
    find ${B} -name Makefile | xargs sed -i s:'-I/usr/include':'-I${STAGING_INCDIR}':g
}

do_compile_append() {
    sed -i -e s:${STAGING_DIR_TARGET}::g \
           -e s:/${TARGET_SYS}::g \
              libxklavier.pc
}


