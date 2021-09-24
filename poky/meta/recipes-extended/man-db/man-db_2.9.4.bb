SUMMARY = "An implementation of the standard Unix documentation system accessed using the man command"
HOMEPAGE = "http://man-db.nongnu.org/"
DESCRIPTION = "man-db is an implementation of the standard Unix documentation system accessed using the man command. It uses a Berkeley DB database in place of the traditional flat-text whatis databases."
LICENSE = "LGPLv2.1 & GPLv2"
LIC_FILES_CHKSUM = "file://docs/COPYING.LIB;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                    file://docs/COPYING;md5=eb723b61539feef013de476e68b5c50a"

SRC_URI = "${SAVANNAH_NONGNU_MIRROR}/man-db/man-db-${PV}.tar.xz \
           file://99_mandb \
           file://man_db.conf-avoid-multilib-install-file-conflict.patch"
SRC_URI[sha256sum] = "b66c99edfad16ad928c889f87cf76380263c1609323c280b3a9e6963fdb16756"

# remove at next version upgrade or when output changes
PR = "r1"
HASHEQUIV_HASH_VERSION .= ".2"

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
		install -m 0644 ${WORKDIR}/99_mandb ${D}/etc/default/volatiles
	fi
}

do_install:append:libc-musl() {
        rm -f ${D}${libdir}/charset.alias
}

FILES:${PN} += "${prefix}/lib/tmpfiles.d"

FILES:${PN}-dev += "${libdir}/man-db/libman.so ${libdir}/${BPN}/libmandb.so"

RDEPENDS:${PN} += "groff"
RRECOMMENDS:${PN} += "less"
RPROVIDES:${PN} += " man"

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
