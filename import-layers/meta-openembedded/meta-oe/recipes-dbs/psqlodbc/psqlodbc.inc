SUMMARY = "ODBC driver for PostgreSQL"
DESCRIPTION = "\
 This package provides a driver that allows ODBC-enabled applications to \
 access PostgreSQL databases.  ODBC is an abstraction layer that allows \
 applications written for that layer to access databases in a manner \
 that is relatively independent of the particular database management \
 system. \
 . \
 You need to install this package if you want to use an application that \
 provides database access through ODBC and you want that application to \
 access a PostgreSQL database.  This package would need to be installed \
 on the same machine as that client application; the PostgreSQL database \
 server can be on a different machine and does not need any additional \
 software to accept ODBC clients. \
"
SECTION = "libs"
HOMEPAGE = "http://psqlodbc.projects.postgresql.org/"

DEPENDS += "postgresql unixodbc"

EXTRA_OECONF = "\
    ac_cv_lib_ltdl_lt_dlopen=no \
    ac_cv_lib_pq_PQconnectdb=yes \
    --with-unixodbc=yes \
    --with-libpq=${STAGING_LIBDIR}/.. \
    --enable-pthreads \
    --disable-unicode \
    LIBS="-lpthread" \
"

inherit autotools pkgconfig ptest

do_compile_ptest() {
    oe_runmake -C ${B}/test
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}
    cp -a --no-preserve=ownership ${B}/test/exe ${S}/test/expected ${D}${PTEST_PATH}
    install -m 0755 ${B}/test/reset-db ${D}${PTEST_PATH}
    install -m 0755 ${B}/test/runsuite ${D}${PTEST_PATH}
    install -m 0755 ${S}/test/odbcini-gen.sh ${D}${PTEST_PATH}
    install -m 0755 ${S}/test/sampletables.sql ${D}${PTEST_PATH}
    sed -i -e 's|@LIBDIR@|${libdir}|' ${D}${PTEST_PATH}/odbcini-gen.sh
}

FILES_${PN} += "${libdir}"

# The tests need a local PostgreSQL server running
RDEPENDS_${PN}-ptest = "postgresql"
