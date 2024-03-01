require mariadb.inc

inherit ptest
inherit useradd

SRC_URI += "${@bb.utils.contains('PTEST_ENABLED', '1', 'file://run-ptest', '', d)}"
DEPENDS += "${@bb.utils.contains('PTEST_ENABLED', '1', 'rsync-native', '', d)}"
RDEPENDS:${PN}-ptest += "cmake sed perl-module-test-more"

do_install_ptest () {
    rsync -a ${B}/unittest ${B}/dbug ${D}${PTEST_PATH} \
          --exclude CMakeFiles \
          --exclude cmake_install.cmake \
          --exclude Makefile \
          --exclude=*.a \
          --exclude=*.h \
          --exclude=*.o \
          --exclude=*.so \
          --exclude=*.d \
          --exclude=*.txt
    install -m 0755 -d ${D}${PTEST_PATH}/storage
    rsync -a ${B}/storage/maria ${B}/storage/perfschema ${B}/storage/innobase ${D}${PTEST_PATH}/storage \
          --exclude CMakeFiles \
          --exclude cmake_install.cmake \
          --exclude Makefile \
          --exclude=*.a \
          --exclude=*.h \
          --exclude=*.o \
          --exclude=*.so \
          --exclude=*.d \
          --exclude=*.txt
    cp -r ${B}/CTestTestfile.cmake ${D}${PTEST_PATH}
    sed -i -e 's#${WORKDIR}##g' `find ${D}${PTEST_PATH} -name CTestTestfile.cmake`
}

DEPENDS += "mariadb-native bison-native boost libpcre2 curl ncurses \
            zlib libaio libedit libevent libxml2 gnutls fmt lzo zstd"

PROVIDES += "mysql5 libmysqlclient"

USERADD_PACKAGES = "${PN}-setupdb"
USERADD_PARAM:${PN}-setupdb = "--system --home-dir /var/mysql -g mysql --shell /bin/false mysql"
GROUPADD_PARAM:${PN}-setupdb = "--system mysql"

RPROVIDES:${PN} += "mysql5"
RREPLACES:${PN} += "mysql5"
RCONFLICTS:${PN} += "mysql5"

RPROVIDES:${PN}-dbg += "mysql5-dbg"
RREPLACES:${PN}-dbg += "mysql5-dbg"
RCONFLICTS:${PN}-dbg += "mysql5-dbg"

RPROVIDES:${PN}-leftovers += "mysql5-leftovers"
RREPLACES:${PN}-leftovers += "mysql5-leftovers"
RCONFLICTS:${PN}-leftovers += "mysql5-leftovers"

RPROVIDES:${PN}-client += "mysql5-client"
RREPLACES:${PN}-client += "mysql5-client"
RCONFLICTS:${PN}-client += "mysql5-client"

RPROVIDES:${PN}-server += "mysql5-server"
RREPLACES:${PN}-server += "mysql5-server"
RCONFLICTS:${PN}-server += "mysql5-server"
