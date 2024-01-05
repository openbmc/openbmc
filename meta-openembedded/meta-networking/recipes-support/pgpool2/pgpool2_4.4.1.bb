SUMMARY = "a language independent connection pool server for PostgreSQL."

DESCRIPTION = "Pgpool-II is a middleware that works between PostgreSQL \
               servers and a PostgreSQL database client. It is distributed \
               under a license similar to BSD and MIT. It provides the \
               following features."

HOMEPAGE = "http://pgpool.net"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=e4b38de086d73e0521de0bbdbaa4a1a9"

SRC_URI = "http://www.pgpool.net/mediawiki/images/pgpool-II-${PV}.tar.gz \
	   file://0001-Fix-build-error-when-build-this-file.patch \
	   file://0001-pgpool2-Fix-unknown-type-name-fd_set-error-with-musl.patch \
	   file://pgpool.sysconfig \
	   file://pgpool.service \
           "
SRC_URI[sha256sum] = "4b379bbba8e178128a1cee4a5bd1ae116dedb3da6121b728c18f0f54c881f328"

S = "${WORKDIR}/pgpool-II-${PV}"

inherit autotools-brokensep systemd

SYSTEMD_SERVICE:${PN} = "pgpool.service"

PACKAGECONFIG ??= " openssl libmemcached postgresql \
"
PACKAGECONFIG[pam] = "--with-pam,,libpam"
PACKAGECONFIG[openssl] = "--with-openssl,,openssl"
PACKAGECONFIG[libmemcached] = "--with-memcached=${STAGING_INCDIR}/libmemcachedutil-1.0,,libmemcached"
PACKAGECONFIG[postgresql] = "--with-pgsql-includedir=${STAGING_INCDIR}/postgresql,, postgresql"

EXTRA_OECONF += "--disable-static \
	         --disable-rpath \
	        "

CFLAGS:append = " -fcommon "

FILES:${PN} += "${datadir}/pgpool-II/ "

do_configure:append() {
    echo "#define HAVE_STRCHRNUL 1" >> ${S}/src/include/config.h
    sed -i "s,#define USE_REPL_SNPRINTF 1,/* #undef USE_REPL_SNPRINTF*/,g" ${S}/src/include/config.h
}

do_install:append() {
    install -d ${D}${sysconfdir}/pgpool-II
    install -D -m 0644 ${WORKDIR}/pgpool.sysconfig  ${D}${sysconfdir}/pgpool-II/pgpool.conf
    install -D -m 0644 ${S}/src/sample/pcp.conf.sample ${D}${sysconfdir}/pgpool-II/pcp.conf
    install -D -m 0644 ${S}/src/sample/pool_hba.conf.sample ${D}${sysconfdir}/pgpool-II/pool_hba.conf
    install -Dm 0644 ${WORKDIR}/pgpool.service ${D}${systemd_system_unitdir}/pgpool.service
}
