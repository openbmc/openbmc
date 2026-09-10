DESCRIPTION = "The C++ Database Access Library"
HOMEPAGE = "http://soci.sourceforge.net"
LICENSE = "BSL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE_1_0.txt;md5=e4224ccaecb14d942c71d31bef20d78c"
SECTION = "libs"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/${BPN}/${BPN}/${BP}/${BP}.tar.gz \
           "
SRC_URI[sha256sum] = "d9bccc834546afab4ab13774c4c80a140b9effe63a1c01819d68c4c29a96df3d"

# Releases live in per-version subdirectories on SourceForge and the tarball
# itself is not listed on any single browsable page, so match the directory names
UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/soci/files/soci/"
UPSTREAM_CHECK_REGEX = "soci-(?P<pver>\d+(\.\d+)+)/"

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
    sed -i 's|${RECIPE_SYSROOT}${prefix}|${_IMPORT_PREFIX}|g' ${D}${libdir}/cmake/${BPN}-${PV}/SOCI*Targets.cmake
}
