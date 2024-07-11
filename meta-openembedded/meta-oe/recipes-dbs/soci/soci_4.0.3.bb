DESCRIPTION = "The C++ Database Access Library"
HOMEPAGE = "http://soci.sourceforge.net"
LICENSE = "BSL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE_1_0.txt;md5=e4224ccaecb14d942c71d31bef20d78c"
SECTION = "libs"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/${BPN}/${BPN}/${BP}/${BP}.tar.gz \
           file://0001-Do-not-use-std-shuffle-with-clang-15.patch \
           "
SRC_URI[sha256sum] = "615e5f7e4b52007f3a3b4050a99aadf6346b56b5098eb08b3a650836083c6a33"

TESTCONFIG = '-DSOCI_TEST_EMPTY_CONNSTR="dummy" -DSOCI_TEST_SQLITE3_CONNSTR="test.db" \
              -DSOCI_TEST_POSTGRESQL_CONNSTR:STRING="dbname=soci_test" \
              -DSOCI_TEST_MYSQL_CONNSTR:STRING="db=soci_test user=oe password=oe"'

OBASEDIR ?= "/opt/oracle"
OINCDIR = "rdbms/public"
OLIBDIR = "lib"

PACKAGECONFIG[sqlite3] = "-DWITH_SQLITE3=ON,-DWITH_SQLITE3=OFF,sqlite3,"
PACKAGECONFIG[mysql] = "-DWITH_MYSQL=ON,-DWITH_MYSQL=OFF,mariadb,"
PACKAGECONFIG[postgresql] = "-DWITH_POSTGRESQL=ON,-DWITH_POSTGRESQL=OFF,postgresql,"
PACKAGECONFIG[odbc] = "-DWITH_ODBC=ON,-DWITH_ODBC=OFF,,"
PACKAGECONFIG[empty] = "-DSOCI_EMPTY=ON,-DSOCI_EMPTY=OFF,,"
PACKAGECONFIG[oracle] = "-DWITH_ORACLE=ON --with-oracle-include=${OINCDIR} --with-oracle-lib=${OLIBDIR},-DWITH_ORACLE=OFF,,"
PACKAGECONFIG[firebird] = "-DWITH_FIREBIRD=ON,-DWITH_FIREBIRD=OFF,,"
PACKAGECONFIG[boost] = "-DWITH_BOOST=ON,-DWITH_BOOST=OFF,boost"
PACKAGECONFIG[ptest] = "${TESTCONFIG},-DSOCI_TESTS=OFF,,"

# enable your backend by default we enable 'empty'
PACKAGECONFIG ??= "boost empty"

EXTRA_OECMAKE = "-DWITH_DB2=OFF"
DISABLE_STATIC = ""

inherit dos2unix cmake

PACKAGES += "${PN}-sqlite3 ${PN}-mysql ${PN}-postgresql ${PN}-odbc ${PN}-oracle"

FILES:${PN}-sqlite3 = "${libdir}/lib${BPN}_sqlite3.so.*"
FILES:${PN}-mysql = "${libdir}/lib${BPN}_mysql.so.*"
FILES:${PN}-postgresql = "${libdir}/lib${BPN}_postgresql.so.*"
FILES:${PN}-odbc = "${libdir}/lib${BPN}_odbc.so.*"
FILES:${PN}-oracle = "${libdir}/lib${BPN}_oracle.so.*"

do_install:append() {
    sed -i 's|${RECIPE_SYSROOT}${prefix}|${_IMPORT_PREFIX}|g' ${D}${libdir}/cmake/SOCI/SOCITargets*.cmake
}
