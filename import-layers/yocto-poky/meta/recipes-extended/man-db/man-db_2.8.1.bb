SUMMARY = "An implementation of the standard Unix documentation system accessed using the man command"
HOMEPAGE = "http://man-db.nongnu.org/"
LICENSE = "LGPLv2.1 & GPLv2"
LIC_FILES_CHKSUM = "file://docs/COPYING.LIB;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                    file://docs/COPYING;md5=eb723b61539feef013de476e68b5c50a"

SRC_URI = "${SAVANNAH_NONGNU_MIRROR}/man-db/man-db-${PV}.tar.xz"
SRC_URI[md5sum] = "51842978e06686286421f9498d1009b7"
SRC_URI[sha256sum] = "a6aec641ca3d9942b054cc0e9c3f05cb46a3a992bc0006795755e2fed1357f3e"

DEPENDS = "libpipeline gdbm groff-native"

# | /usr/src/debug/man-db/2.8.0-r0/man-db-2.8.0/src/whatis.c:939: undefined reference to `_nl_msg_cat_cntr'
USE_NLS_libc-musl = "no"

inherit gettext pkgconfig autotools

EXTRA_OECONF = "--with-pager=less --disable-cache-owner"

do_install_append_libc-musl() {
        rm -f ${D}${libdir}/charset.alias
}

FILES_${PN} += "${prefix}/lib/tmpfiles.d"

FILES_${PN}-dev += "${libdir}/man-db/libman.so ${libdir}/${BPN}/libmandb.so"

RDEPENDS_${PN} += "groff"
RRECOMMENDS_${PN} += "less"
RPROVIDES_${PN} += " man"

def compress_pkg(d):
    if bb.utils.contains("INHERIT", "compress_doc", True, False, d):
         compress = d.getVar("DOC_COMPRESS")
         if compress == "gz":
             return "gzip"
         elif compress == "bz2":
             return "bzip2"
         elif compress == "xz":
             return "xz"
    return ""

RDEPENDS_${PN} += "${@compress_pkg(d)}"
