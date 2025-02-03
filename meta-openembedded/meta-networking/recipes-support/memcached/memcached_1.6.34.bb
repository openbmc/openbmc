SUMMARY = "A high-performance memory object caching system"
DESCRIPTION = "\
 memcached optimizes specific high-load serving applications that are designed \
 to take advantage of its versatile no-locking memory access system. Clients \
 are available in several different programming languages, to suit the needs \
 of the specific application. Traditionally this has been used in mod_perl \
 apps to avoid storing large chunks of data in Apache memory, and to share \
 this burden across several machines."
SECTION = "web"
HOMEPAGE = "http://memcached.org/"
LICENSE = "BSD-3-Clause"

LIC_FILES_CHKSUM = "file://COPYING;md5=7e5ded7363d335e1bb18013ca08046ff"

inherit autotools pkgconfig

DEPENDS += "libevent"
RDEPENDS:${PN} += "perl perl-module-posix perl-module-autoloader \
    perl-module-tie-hash bash \
    "

SRC_URI = "http://www.memcached.org/files/${BP}.tar.gz \
           file://memcached-add-hugetlbfs-check.patch \
           "
SRC_URI[sha256sum] = "0d5380e2e0a5b4fcef1d89a368a11c4f06686c6017c1fff778b3b4578f0674ec"

CVE_STATUS[CVE-2022-26635] = "disputed: this is a problem of applications using php-memcached inproperly"

UPSTREAM_CHECK_URI = "${HOMEPAGE}"

# set the same COMPATIBLE_HOST as libhugetlbfs
COMPATIBLE_HOST = "(i.86|x86_64|powerpc|powerpc64|aarch64|arm).*-linux*"

# assoc.c:83:9: error: variable 'depth' set but not used [-Werror,-Wunused-but-set-variable]
CFLAGS:append:toolchain-clang = " -Wno-error=unused-but-set-variable"

python __anonymous () {
    endianness = d.getVar('SITEINFO_ENDIANNESS')
    if endianness == 'le':
        d.appendVar('EXTRA_OECONF', " ac_cv_c_endian=little")
    else:
        d.appendVar('EXTRA_OECONF', " ac_cv_c_endian=big")
}

PACKAGECONFIG ??= ""
PACKAGECONFIG[hugetlbfs] = "--enable-hugetlbfs, --disable-hugetlbfs, libhugetlbfs"

inherit update-rc.d

INITSCRIPT_NAME = "memcached"
INITSCRIPT_PARAMS = "defaults"

do_install:append() {
    install -D -m 755 ${S}/scripts/memcached-init ${D}${sysconfdir}/init.d/memcached
    mkdir -p ${D}/usr/share/memcached/scripts
    install -m 755 ${S}/scripts/memcached-tool ${D}/usr/share/memcached/scripts
    install -m 755 ${S}/scripts/start-memcached ${D}/usr/share/memcached/scripts
}
