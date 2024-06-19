SUMMARY = "An implementation of the standard Unix documentation system accessed using the man command"
HOMEPAGE = "http://man-db.nongnu.org/"
DESCRIPTION = "man-db is an implementation of the standard Unix documentation system accessed using the man command. It uses a Berkeley DB database in place of the traditional flat-text whatis databases."
LICENSE = "LGPL-2.1-or-later & GPL-2.0-or-later & GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464 \
                    file://docs/COPYING.GPLv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://docs/COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
                   "

SRC_URI = "${SAVANNAH_NONGNU_MIRROR}/man-db/man-db-${PV}.tar.xz \
           file://99_mandb \
           file://0001-man-Move-local-variable-declaration-to-function-scop.patch \
          "
SRC_URI[sha256sum] = "ddee249daeb78cf92bab794ccd069cc8b575992265ea20e239e887156e880265"

DEPENDS = "libpipeline gdbm groff-native base-passwd"
RDEPENDS:${PN} += "base-passwd"
PACKAGE_WRITE_DEPS += "base-passwd"

# | /usr/src/debug/man-db/2.8.0-r0/man-db-2.8.0/src/whatis.c:939: undefined reference to `_nl_msg_cat_cntr'
USE_NLS:libc-musl = "no"

inherit gettext pkgconfig autotools systemd

EXTRA_OECONF = "--with-pager=less --with-systemdsystemunitdir=${systemd_system_unitdir}"
EXTRA_AUTORECONF += "-I ${S}/gl/m4"

PACKAGECONFIG[bzip2] = "--with-bzip2=bzip2,ac_cv_prog_have_bzip2='',bzip2"
PACKAGECONFIG[gzip] = "--with-gzip=gzip,ac_cv_prog_have_gzip='',gzip"
PACKAGECONFIG[lzip] = "--with-lzip=lzip,ac_cv_prog_have_lzip='',lzip"
PACKAGECONFIG[lzma] = "--with-lzma=lzma,ac_cv_prog_have_lzma='',xz"
PACKAGECONFIG[zstd] = "--with-zstd=zstd,ac_cv_prog_have_zstd='',zstd"
PACKAGECONFIG[xz] = "--with-xz=xz,ac_cv_prog_have_xz='',xz"

do_install() {
	autotools_do_install

	if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
	        install -d ${D}/etc/default/volatiles
		install -m 0644 ${UNPACKDIR}/99_mandb ${D}/etc/default/volatiles
	fi
}

do_install:append:libc-musl() {
        rm -f ${D}${libdir}/charset.alias
}

FILES:${PN} += "${prefix}/lib/tmpfiles.d"

FILES:${PN}-dev += "${libdir}/man-db/libman.so ${libdir}/${BPN}/libmandb.so"

RDEPENDS:${PN} += "groff"
RRECOMMENDS:${PN} += "less"
# iconv from glibc-utils can be used to transform encoding
RRECOMMENDS:${PN}:append:libc-glibc = " glibc-utils"
RPROVIDES:${PN} += "man"

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

RDEPENDS:${PN} += "${@compress_pkg(d)}"

SYSTEMD_SERVICE:${PN} = "man-db.timer man-db.service"
SYSTEMD_AUTO_ENABLE ?= "disable"
