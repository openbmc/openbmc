# libdbi-drivers OE build file
# Copyright (C) 2005, Koninklijke Philips Electronics NV.  All Rights Reserved
# Released under the MIT license (see packages/COPYING)

DESCRIPTION = "Database Drivers for libdbi"
HOMEPAGE = "http://libdbi-drivers.sourceforge.net/"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499"
SECTION = "libs"

PROVIDES = "libdbd-sqlite"
DEPENDS = "libdbi sqlite sqlite3 mysql5 postgresql"

SRC_URI = "${SOURCEFORGE_MIRROR}/libdbi-drivers/libdbi-drivers-${PV}.tar.gz"

inherit autotools

PACKAGES += "libdbd-sqlite3 libdbd-sqlite libdbd-pgsql libdbd-mysql"

EXTRA_OECONF = "--with-dbi-incdir=${STAGING_INCDIR} \
                --with-dbi-libdir=${STAGING_LIBDIR} \
                --with-sqlite \
                --with-sqlite3 \
                --with-pgsql \
                --with-mysql \
                --with-sqlite-libdir=${STAGING_LIBDIR} \
                --with-sqlite-incdir=${STAGING_INCDIR} \
                --with-sqlite3-incdir=${STAGING_INCDIR} \
                --with-sqlite3-libdir=${STAGING_LIBDIR} \
                --with-mysql-incdir=${STAGING_INCDIR} \
                --with-mysql-libdir=${STAGING_LIBDIR} \
                --with-pgsql-incdir=${STAGING_INCDIR} \
                --with-pgsql-libdir=${STAGING_LIBDIR} \
                --disable-docs"

FILES_${PN}-dbg += " ${libdir}/dbd/.debug/*.so"
FILES_${PN}-dev += " ${libdir}/dbd/*.la"
FILES_${PN}-staticdev += " ${libdir}/dbd/*.a"

DESCRIPTION_libdbd-sqlite = "SQLite database driver for libdbi"
FILES_libdbd-sqlite = "${libdir}/dbd/libdbdsqlite.so"

DESCRIPTION_libdbd-sqlite3 =  "SQLite3 database driver for libdbi"
FILES_libdbd-sqlite3 = "${libdir}/dbd/libdbdsqlite3.so"

DESCRIPTION_libdbd-mysql = "MySQL4 database driver for libdbi"
FILES_libdbd-mysql = "${libdir}/dbd/libdbdmysql.so"

DESCRIPTION_libdbd-psql = "Postgres SQL database driver for libdbi"
FILES_libdbd-pgsql = "${libdir}/dbd/libdbdpgsql.so"

