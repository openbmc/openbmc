# Copyright (C) 2015 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "The C++ Database Access Library"
HOMEPAGE = "http://soci.sourceforge.net"
LICENSE = "BSL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE_1_0.txt;md5=e4224ccaecb14d942c71d31bef20d78c"
SECTION = "libs"
DEPENDS = "boost"


SRC_URI = "${SOURCEFORGE_MIRROR}/project/${BPN}/${BPN}/${BP}/${BP}.tar.gz \
           file://soci_libdir.patch \
          "
SRC_URI[md5sum] = "bc3c35f6cd3f80a1be19fe30b3c111bf"
SRC_URI[sha256sum] = "30ea2f4d26639ef5b5cdc24fb300b3896746243dbb77b743582de2ff443ccb1c"

TESTCONFIG = '-DSOCI_TEST_EMPTY_CONNSTR="dummy" -DSOCI_TEST_SQLITE3_CONNSTR="test.db" \
              -DSOCI_TEST_POSTGRESQL_CONNSTR:STRING="dbname=soci_test" \
              -DSOCI_TEST_MYSQL_CONNSTR:STRING="db=soci_test user=oe password=oe"'

OBASEDIR ?= "/opt/oracle"
OINCDIR = "rdbms/public"
OLIBDIR = "lib"

PACKAGECONFIG[sqlite3] = "-DSOCI_SQLITE3=ON,-DSOCI_SQLITE3=OFF,sqlite3,"
PACKAGECONFIG[mysql] = "-DSOCI_MYSQL=ON,-DSOCI_MYSQL=OFF,mariadb,"
PACKAGECONFIG[postgresql] = "-DSOCI_POSTGRESQL=ON,-DSOCI_POSTGRESQL=OFF,postgresql,"
PACKAGECONFIG[odbc] = "-DSOCI_ODBC=ON,-DSOCI_ODBC=OFF,,"
PACKAGECONFIG[empty] = "-DSOCI_EMPTY=ON,-DSOCI_EMPTY=OFF,,"
PACKAGECONFIG[oracle] = "-DWITH_ORACLE=ON --with-oracle-include=${OINCDIR} --with-oracle-lib=${OLIBDIR},-DWITH_ORACLE=OFF,,"
PACKAGECONFIG[ptest] = "${TESTCONFIG},,,"

# enable your backend by default we enable 'empty'
PACKAGECONFIG ??= "empty"

# Take the flags added by PACKAGECONFIG and pass them to cmake.
EXTRA_OECMAKE = "${EXTRA_OECONF} -DSOCI_LIBDIR=${libdir}"
DISABLE_STATIC = ""

inherit cmake

PACKAGES += "${PN}-sqlite3 ${PN}-mysql ${PN}-postgresql ${PN}-odbc ${PN}-oracle"

FILES_${PN}-sqlite3 = "${libdir}/lib${BPN}_sqlite3.so.*"
FILES_${PN}-mysql = "${libdir}/lib${BPN}_mysql.so.*"
FILES_${PN}-postgresql = "${libdir}/lib${BPN}_postgresql.so.*"
FILES_${PN}-odbc = "${libdir}/lib${BPN}_odbc.so.*"
FILES_${PN}-oracle = "${libdir}/lib${BPN}_oracle.so.*"
