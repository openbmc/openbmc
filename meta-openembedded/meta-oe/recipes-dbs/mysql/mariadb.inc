SUMMARY = "A robust, scalable, and reliable SQL server"
HOMEPAGE = "http://mariadb.org"
SECTION = "libs"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b1becf0cfa3366e0f4d854d1d264f311"

SRC_URI = "https://archive.mariadb.org/${BP}/source/${BP}.tar.gz \
           file://my.cnf \
           file://mysqld.service \
           file://install_db.service \
           file://install_db \
           file://mysql-systemd-start \
           file://configure.cmake-fix-valgrind.patch \
           file://support-files-CMakeLists.txt-fix-do_populate_sysroot.patch \
           file://fix-arm-atomic.patch \
           file://0001-Fix-library-LZ4-lookup.patch \
           file://0001-innobase-Define-__NR_futex-if-it-does-not-exist.patch \
           file://0001-aio_linux-Check-if-syscall-exists-before-using-it.patch \
           file://ssize_t.patch \
           file://mm_malloc.patch \
           file://sys_futex.patch \
           file://cross-compiling.patch \
           file://0001-sql-CMakeLists.txt-fix-gen_lex_hash-not-found.patch \
           file://lfs64.patch \
           file://0001-Add-missing-includes-cstdint-and-cstdio.patch \
          "
SRC_URI:append:libc-musl = " file://ppc-remove-glibc-dep.patch"
SRC_URI[sha256sum] = "1c0163463e98d71f4780741611a40981eee2bc44d392601ca49bbf948d04dd67"

UPSTREAM_CHECK_URI = "https://github.com/MariaDB/server/releases"

S = "${WORKDIR}/mariadb-${PV}"

BINCONFIG_GLOB = "mysql_config"

inherit cmake gettext binconfig update-rc.d systemd multilib_script pkgconfig

MULTILIB_SCRIPTS = "${PN}-server:${bindir}/mariadbd-safe \
                    ${PN}-setupdb:${bindir}/mariadb-install-db"

INITSCRIPT_PACKAGES = "${PN}-server ${PN}-setupdb"
INITSCRIPT_NAME:${PN}-server = "mysqld"
INITSCRIPT_PARAMS:${PN}-server ?= "start 45 5 . stop 45 0 6 1 ."

INITSCRIPT_NAME:${PN}-setupdb = "install_db"
INITSCRIPT_PARAMS:${PN}-setupdb ?= "defaults 44 44"

SYSTEMD_PACKAGES = "${PN}-server ${PN}-setupdb"
SYSTEMD_SERVICE:${PN}-server = "mysqld.service"
SYSTEMD_AUTO_ENABLE:${PN}-server ?= "disable"

SYSTEMD_SERVICE:${PN}-setupdb = "install_db.service"
SYSTEMD_AUTO_ENABLE:${PN}-setupdb ?= "enable"
ALLOW_EMPTY:${PN}-setupdb ?= "1"
FILES:${PN}-setupdb = "${sysconfdir}/init.d/install_db \
                       ${bindir}/mariadb-install-db \
                       ${bindir}/my_print_defaults \
                       ${bindir}/mysql_install_db \
                       ${bindir}/mysql-systemd-start \
                       "

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)} lz4 openssl"
EXTRA_OEMAKE = "'GEN_LEX_HASH=${STAGING_BINDIR_NATIVE}/gen_lex_hash'"

PACKAGECONFIG:class-native = ""
PACKAGECONFIG[pam] = "-DPLUGIN_AUTH_PAM=YES,-DPLUGIN_AUTH_PAM=NO,libpam"
PACKAGECONFIG[valgrind] = "-DWITH_VALGRIND=TRUE,-DWITH_VALGRIND=FALSE,valgrind"
PACKAGECONFIG[krb5] = ", ,krb5"
PACKAGECONFIG[lz4] = ", ,lz4"
PACKAGECONFIG[openssl] = "-DWITH_SSL='system',-DWITH_SSL='bundled',openssl"

# MariaDB doesn't link properly with gold
# https://mariadb.atlassian.net/browse/MDEV-5982
TARGET_CFLAGS += "-fuse-ld=bfd"
LDFLAGS += " -pthread"

BUILD_CFLAGS += "-fuse-ld=bfd"
BUILD_CXXFLAGS += "-fuse-ld=bfd"

LDFLAGS:x86:toolchain-clang = "-latomic"

EXTRA_OECMAKE = "-DWITH_EMBEDDED_SERVER=ON \
                 -DWITH_JEMALLOC=no \
                 -DWITHOUT_TOKUDB=TRUE \
                 -DCMAKE_DISABLE_FIND_PACKAGE_Boost=TRUE \
                 -DGROFF=FALSE \
                 -DNROFF=FALSE \
                 -DENABLE_DTRACE=FALSE \
                 -DWITH_PIC=ON \
                 -DWITH_PCRE=system \
                 -DINSTALL_LAYOUT=RPM \
                 -DINSTALL_DOCDIR:PATH=${datadir}/doc/${BPN} \
                 -DINSTALL_LIBDIR:PATH=${baselib} \
                 -DINSTALL_PLUGINDIR:PATH=${baselib}/plugin \
                 -DINSTALL_SYSCONFDIR:PATH=${sysconfdir} \
                 -DMYSQL_DATADIR:PATH=/var/mysql \
                 -DCAT_EXECUTABLE=`which cat` \
                 -DSTACK_DIRECTION=1 \
                 -DCMAKE_AR:FILEPATH=${AR}"


# With Ninja it fails with:
# make: *** No rule to make target `install'.  Stop.
OECMAKE_GENERATOR = "Unix Makefiles"

ARM_INSTRUCTION_SET:armv4 = "arm"
ARM_INSTRUCTION_SET:armv5 = "arm"

do_compile:prepend:class-target () {
    # remove the buildpath
    sed -i -e "s:CMAKE_SYSROOT:CMAKE_SYSROOT_PLACE_HOLDER:g" ${S}/libmariadb/mariadb_config/mariadb_config.c.in
}

do_configure:append() {
    # handle distros with different values of ${libexecdir}
    libexecdir2=`echo ${libexecdir} | sed -e 's+/usr/++g'`
    sed -i -e "s:/libexec:/$libexecdir2:g" ${S}/scripts/mysql_install_db.sh
    sed -i -e "s:mysqld libexec:mysqld $libexecdir2:g" ${S}/scripts/mysql_install_db.sh
    sed -i -e "s:/libexec:/$libexecdir2:g" ${S}/scripts/mysqld_safe.sh
}

do_generate_toolchain_file:append:class-native () {
    # If these are set cmake will assume we're cross-compiling, which will
    # result in certain things we want being disabled
    sed -i "/set( CMAKE_SYSTEM_NAME/d" ${WORKDIR}/toolchain.cmake
    sed -i "/set( CMAKE_SYSTEM_PROCESSOR/d" ${WORKDIR}/toolchain.cmake
}


do_compile:prepend:class-target () {
    # These need to be in-tree or make will think they need to be built,
    # and since we're cross-compiling that is disabled
    cp ${STAGING_BINDIR_NATIVE}/comp_err ${S}/extra
    cp ${STAGING_BINDIR_NATIVE}/comp_sql ${S}/scripts
    if [ "${@bb.utils.contains('PACKAGECONFIG', 'krb5', 'yes', 'no', d)}" = "no" ]; then
        if ! [ -e ${B}/include/openssl/kssl.h ] ; then
            mkdir -p ${B}/include/openssl
            echo "#ifndef KSSL_H" >${B}/include/openssl/kssl.h
            echo "#define KSSL_H" >>${B}/include/openssl/kssl.h
            echo "#include <openssl/opensslconf.h>">>${B}/include/openssl/kssl.h
            echo "#endif" >>${B}/include/openssl/kssl.h
        fi
    fi
    install -D ${RECIPE_SYSROOT_NATIVE}/usr/bin/uca-dump ${B}/strings/uca-dump
}

SYSROOT_PREPROCESS_FUNCS += "mariadb_sysroot_preprocess"

# We need to append this so it runs *after* binconfig's preprocess function
#
# We really don't care exactly what the directories were set to originally.
# plugindir is not fixed, but we don't create any plugins.
#
mariadb_sysroot_preprocess () {
    sed -i -es,^pkgincludedir=.*,pkgincludedir=\'${STAGING_INCDIR}/mysql\', ${SYSROOT_DESTDIR}${bindir_crossscripts}/mysql_config
    sed -i -es,^pkglibdir=.*,pkglibdir=\'${STAGING_LIBDIR}\', ${SYSROOT_DESTDIR}${bindir_crossscripts}/mysql_config
}

do_install() {
    oe_runmake 'DESTDIR=${D}' install

    install -d ${D}/${sysconfdir}/init.d
    install -m 0644 ${WORKDIR}/my.cnf ${D}/${sysconfdir}/
    install -m 0755 ${WORKDIR}/install_db ${D}/${sysconfdir}/init.d/
    mv ${D}/${sysconfdir}/init.d/mysql ${D}/${sysconfdir}/init.d/mysqld

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/mysqld.service ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/install_db.service ${D}${systemd_unitdir}/system
    sed -i -e 's,@BINDIR@,${bindir},g' -e 's,@PREFIX@,${prefix},g' ${D}${systemd_unitdir}/system/mysqld.service \
                                                                   ${D}${systemd_unitdir}/system/install_db.service

    install -d ${D}${bindir}
    install -m 755 ${WORKDIR}/mysql-systemd-start ${D}${bindir}
    install -d ${D}${datadir}/doc/${PN}
    if [ -f ${D}${datadir}/doc/README ]; then
        mv ${D}${datadir}/doc/README ${D}${datadir}/doc/${PN}/
    fi

    # mini-benchmark used for Gitlab-CI to run on every commit to catch
    # if there are severe performance regressions.
    # remove it to avoid introducing bash dependency
    if [ -f ${D}${datadir}/mysql/mini-benchmark ]; then
        rm -rf ${D}${datadir}/mysql/mini-benchmark
    fi
    if ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'true', 'false', d)}; then
        pam_so=$(find ${D} -name pam_user_map.so)
        if [ x"${pam_so}" != x ]; then
            pam_dir=$(dirname ${pam_so})
            mv ${pam_dir} ${D}/${libdir}
            rmdir --ignore-fail-on-non-empty ${pam_dir%security}
        fi
    fi
}

PACKAGES = "${PN}-dbg ${PN}-setupdb ${PN} \
    libmysqlclient-r libmysqlclient-r-dev libmysqlclient-r-staticdev \
    libmysqlclient libmysqlclient-dev libmysqlclient-staticdev \
    libmysqld libmysqld-dev ${PN}-client ${PN}-server ${PN}-leftovers"
CONFFILES:${PN}-server += "${sysconfdir}/my.cnf ${sysconfdir}/my.cnf.d/server.cnf"
CONFFILES:${PN}-client += "${sysconfdir}/my.cnf.d/mysql-clients.cnf"
CONFFILES:libmysqlclient += "${sysconfdir}/my.cnf.d/client.cnf"

RPROVIDES:${PN}-dbg += "libmysqlclient-r-dbg libmysqlclient-dbg"

FILES:${PN} = " "
RDEPENDS:${PN} = "${PN}-client ${PN}-server"
ALLOW_EMPTY:${PN} = "1"

RDEPENDS:${PN}-client = "perl perl-module-getopt-long perl-module-file-temp \
    perl-module-fcntl perl-module-sys-hostname perl-module-ipc-open3 \
    perl-module-exporter"
RDEPENDS:${PN}-server = "perl perl-module-getopt-long perl-module-data-dumper \
    perl-module-file-basename perl-module-file-path perl-module-sys-hostname \
    perl-module-file-copy perl-module-file-temp perl-module-posix \
    ${PN}-client ${PN}-setupdb libdbi-perl libdbd-mysql-perl lzo"
RDEPENDS:${PN}-leftovers = "perl perl-module-cwd perl-module-benchmark perl-module-getopt-long \
    perl-module-posix perl-module-data-dumper perl-module-sigtrap perl-module-threads \
    perl-module-threads-shared perl-module-io-socket perl-module-sys-hostname perl-module-file-copy \
    perl-module-file-spec perl-module-file-find perl-module-file-basename perl-module-file-path \
    perl-module-constant perl-module-lib perl-module-file-temp perl-module-file-spec-functions \
    perl-module-io-socket-inet perl-module-io-select bash"
RDEPENDS:${PN}-setupdb = "coreutils"

# Allow old code to link to the backward compatible library
RDEPENDS:libmysqlclient-dev = "libmysqlclient-r-dev"

PACKAGE_PREPROCESS_FUNCS += "src_package_preprocess"
src_package_preprocess () {
        # Trim build paths from comments in generated sources to ensure reproducibility
        sed -i -e "s,${S}/sql/,,g" \
               -e "s,${B}/sql/,,g" \
            ${B}/sql/yy_oracle.hh \
            ${B}/sql/yy_mariadb.cc \
            ${B}/sql/yy_mariadb.hh \
            ${B}/sql/yy_oracle.cc
}

FILES:libmysqlclient = "\
    ${libdir}/libmysqlclient.so.* \
    ${libdir}/libmariadb.so.* \
    ${sysconfdir}/my.cnf.d/client.cnf"
FILES:libmysqlclient-dev = " \
    ${includedir}/mysql/ \
    ${libdir}/libmysqlclient.so \
    ${libdir}/libmariadb.so \
    ${sysconfdir}/aclocal \
    ${bindir}/mysql_config"
FILES:libmysqlclient-staticdev = "\
    ${libdir}/*.a"

# Avoid warnings about ha_xtradb.so.0.0.0 and ha_innodb_plugin.so.0.0.0
# which are intentionally non-PIC on 32-bit x86 (see e.g.
# storage/xtradb/plug.in in the source)
INSANE_SKIP:libmysqlclient:append:x86 = " textrel"

FILES:libmysqlclient-r = "${libdir}/libmysqlclient_r.so.*"
FILES:libmysqlclient-r-dev = "\
    ${libdir}/libmysqlclient_r.so"
FILES:libmysqlclient-r-staticdev = "${libdir}/libmysqlclient_r.a"

FILES:libmysqld = "\
    ${libdir}/libmysqld.so.* \
    ${libdir}/libmariadbd.so.*"
FILES:libmysqld-dev = "\
    ${libdir}/libmysqld.so \
    ${libdir}/libmariadbd.so"

FILES:${PN}-client = "\
    ${bindir}/myisam_ftdump \
    ${bindir}/mysql \
    ${bindir}/mariadb \
    ${bindir}/mysql_client_test \
    ${bindir}/mariadb-client-test \
    ${bindir}/mysql_client_test_embedded \
    ${bindir}/mariadb-client-test-embedded \
    ${bindir}/mysql_find_rows \
    ${bindir}/mariadb-find-rows \
    ${bindir}/mysql_fix_extensions \
    ${bindir}/mariadb-fix-extensions \
    ${bindir}/mysql_waitpid \
    ${bindir}/mariadb-waitpid \
    ${bindir}/mysqlaccess \
    ${bindir}/mariadb-access \
    ${bindir}/mysqladmin \
    ${bindir}/mariadb-admin \
    ${bindir}/mysqlcheck \
    ${bindir}/mariadb-check \
    ${bindir}/mysqldump \
    ${bindir}/mariadb-dump \
    ${bindir}/mysqldumpslow \
    ${bindir}/mariadb-dumpslow \
    ${bindir}/mysqlimport \
    ${bindir}/mariadb-import \
    ${bindir}/mysqlshow \
    ${bindir}/mariadb-show \
    ${bindir}/mysqlslap \
    ${bindir}/mariadb-slap \
    ${bindir}/mysqltest_embedded \
    ${bindir}/mariadb-test-embedded \
    ${libexecdir}/mysqlmanager \
    ${sysconfdir}/my.cnf.d/mysql-clients.cnf"

FILES:${PN}-server = "\
    ${bindir}/msql2mysql \
    ${bindir}/my_print_defaults \
    ${bindir}/myisamchk \
    ${bindir}/myisamlog \
    ${bindir}/myisampack \
    ${bindir}/mysql_convert_table_format \
    ${bindir}/mariadb-convert-table-format \
    ${bindir}/mysql_install_db \
    ${bindir}/mysql_secure_installation \
    ${bindir}/mariadb-secure-installation \
    ${bindir}/mysql_setpermission \
    ${bindir}/mariadb-setpermission \
    ${bindir}/mysql-systemd-start \
    ${bindir}/mysql_tzinfo_to_sql \
    ${bindir}/mariadb-tzinfo-to-sql \
    ${bindir}/mysql_upgrade \
    ${bindir}/mariadb-upgrade \
    ${bindir}/mysql_plugin \
    ${bindir}/mariadb-plugin \
    ${bindir}/mysqlbinlog \
    ${bindir}/mariadb-binlog \
    ${bindir}/mysqld_multi \
    ${bindir}/mariadbd-multi \
    ${bindir}/mariadbd-safe \
    ${bindir}/mysqld_safe \
    ${bindir}/mysqld_safe_helper \
    ${bindir}/mariadbd-safe-helper \
    ${bindir}/mysqlhotcopy \
    ${bindir}/mariadb-hotcopy \
    ${bindir}/mysqltest \
    ${bindir}/mariadb-test \
    ${bindir}/perror \
    ${bindir}/replace \
    ${bindir}/resolve_stack_dump \
    ${bindir}/resolveip \
    ${libexecdir}/mysqld \
    ${sbindir}/mysqld \
    ${sbindir}/mariadbd \
    ${libdir}/plugin/*.so \
    ${libdir}/security/*.so \
    ${libdir}/plugin/auth_pam_tool_dir/auth_pam_tool \
    ${datadir}/mysql/ \
    ${localstatedir}/mysql/ \
    ${sysconfdir}/init.d/mysqld \
    ${sysconfdir}/my.cnf \
    ${sysconfdir}/my.cnf.d/server.cnf \
    ${sysconfdir}/security/user_map.conf \
    ${sysconfdir}/tmpfiles.d"

DESCRIPTION:${PN}-leftovers = "unpackaged and probably unneeded files for ${PN}"
FILES:${PN}-leftovers = "/"
