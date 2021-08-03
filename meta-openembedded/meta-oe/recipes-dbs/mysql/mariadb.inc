SUMMARY = "A robust, scalable, and reliable SQL server"
HOMEPAGE = "http://mariadb.org"
SECTION = "libs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b1becf0cfa3366e0f4d854d1d264f311"

SRC_URI = "https://downloads.mariadb.org/interstitial/${BP}/source/${BP}.tar.gz \
           file://my.cnf \
           file://mysqld.service \
           file://install_db.service \
           file://install_db \
           file://mysql-systemd-start \
           file://configure.cmake-fix-valgrind.patch \
           file://fix-a-building-failure.patch \
           file://support-files-CMakeLists.txt-fix-do_populate_sysroot.patch \
           file://sql-CMakeLists.txt-fix-gen_lex_hash-not-found.patch \
           file://0001-disable-ucontext-on-musl.patch \
           file://c11_atomics.patch \
           file://clang_version_header_conflict.patch \
           file://fix-arm-atomic.patch \
          "
SRC_URI[md5sum] = "c3bc7a3eca3b0bbae5748f7b22a55c0c"
SRC_URI[sha256sum] = "87d5e29ee1f18de153266ec658138607703ed2a05b3ffb1f89091d33f4abf545"

UPSTREAM_CHECK_URI = "https://github.com/MariaDB/server/releases"

S = "${WORKDIR}/mariadb-${PV}"

BINCONFIG_GLOB = "mysql_config"

inherit cmake gettext binconfig update-rc.d useradd systemd multilib_script

MULTILIB_SCRIPTS = "${PN}-server:${bindir}/mysql_install_db ${PN}-server:${bindir}/mysqld_safe"

INITSCRIPT_PACKAGES = "${PN}-server ${PN}-setupdb"
INITSCRIPT_NAME_${PN}-server = "mysqld"
INITSCRIPT_PARAMS_${PN}-server ?= "start 45 5 . stop 45 0 6 1 ."

USERADD_PACKAGES = "${PN}-server"
USERADD_PARAM_${PN}-server = "--system --home-dir /var/mysql -g mysql --shell /bin/false mysql"
GROUPADD_PARAM_${PN}-server = "--system mysql"

INITSCRIPT_NAME_${PN}-setupdb = "install_db"
INITSCRIPT_PARAMS_${PN}-setupdb ?= "defaults 44 44"

SYSTEMD_PACKAGES = "${PN}-server ${PN}-setupdb"
SYSTEMD_SERVICE_${PN}-server = "mysqld.service"
SYSTEMD_AUTO_ENABLE_${PN}-server ?= "disable"

SYSTEMD_SERVICE_${PN}-setupdb = "install_db.service"
SYSTEMD_AUTO_ENABLE_${PN}-setupdb ?= "enable"
ALLOW_EMPTY_${PN}-setupdb ?= "1"
FILES_${PN}-setupdb = "${sysconfdir}/init.d/install_db"

EXTRA_OEMAKE = "'GEN_LEX_HASH=${STAGING_BINDIR_NATIVE}/gen_lex_hash'"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)} setupdb"
PACKAGECONFIG_class-native = ""
PACKAGECONFIG[pam] = ",-DWITHOUT_AUTH_PAM=TRUE,libpam"
PACKAGECONFIG[valgrind] = "-DWITH_VALGRIND=TRUE,-DWITH_VALGRIND=FALSE,valgrind"
PACKAGECONFIG[krb5] = ", ,krb5"
PACKAGECONFIG[setupdb] = ", ,,${PN}-setupdb"

# MariaDB doesn't link properly with gold
# https://mariadb.atlassian.net/browse/MDEV-5982
TARGET_CFLAGS += "-fuse-ld=bfd"
LDFLAGS += " -pthread"
BUILD_CFLAGS += "-fuse-ld=bfd"
BUILD_CXXFLAGS += "-fuse-ld=bfd"

EXTRA_OECMAKE = "-DWITH_EMBEDDED_SERVER=ON \
                 -DWITH_JEMALLOC=no \
                 -DWITHOUT_TOKUDB=TRUE \
                 -DCMAKE_DISABLE_FIND_PACKAGE_Boost=TRUE \
                 -DGROFF=FALSE \
                 -DNROFF=FALSE \
                 -DENABLE_DTRACE=FALSE \
                 -DWITH_PIC=ON \
                 -DINSTALL_LAYOUT=RPM \
                 -DINSTALL_DOCDIR:PATH=${datadir}/doc/${BPN} \
                 -DINSTALL_LIBDIR:PATH=${baselib} \
                 -DINSTALL_PLUGINDIR:PATH=${baselib}/plugin \
                 -DINSTALL_SYSCONFDIR:PATH=${sysconfdir} \
                 -DMYSQL_DATADIR:PATH=/var/mysql \
                 -DCAT_EXECUTABLE=`which cat` \
                 -DCMAKE_AR:FILEPATH=${AR}"

# With Ninja it fails with:
# make: *** No rule to make target `install'.  Stop.
OECMAKE_GENERATOR = "Unix Makefiles"

ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"

do_configure_append() {
    # handle distros with different values of ${libexecdir}
    libexecdir2=`echo ${libexecdir} | sed -e 's+/usr/++g'`
    sed -i -e "s:/libexec:/$libexecdir2:g" ${S}/scripts/mysql_install_db.sh
    sed -i -e "s:mysqld libexec:mysqld $libexecdir2:g" ${S}/scripts/mysql_install_db.sh
    sed -i -e "s:/libexec:/$libexecdir2:g" ${S}/scripts/mysqld_safe.sh
}

do_generate_toolchain_file_append_class-native () {
    # If these are set cmake will assume we're cross-compiling, which will
    # result in certain things we want being disabled
    sed -i "/set( CMAKE_SYSTEM_NAME/d" ${WORKDIR}/toolchain.cmake
    sed -i "/set( CMAKE_SYSTEM_PROCESSOR/d" ${WORKDIR}/toolchain.cmake
}

do_compile_prepend_class-target () {
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
    # workaround to handle out-of-source build from source package
    yacc_files="sql_yacc.hh sql_yacc.cc sql_yacc_ora.hh sql_yacc_ora.cc"
    for yacc_file in ${yacc_files}; do
        cp ${S}/sql/${yacc_file} ${B}/sql/${yacc_file}
    done
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

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/tmpfiles.d
        echo "f /var/log/mysqld.err 0640 mysql mysql -" \
            > ${D}${sysconfdir}/tmpfiles.d/99-mysqld.conf
    fi
    install -d ${D}${bindir}
    install -m 755 ${WORKDIR}/mysql-systemd-start ${D}${bindir}
    install -d ${D}${datadir}/doc/${PN}
    if [ -f ${D}${datadir}/doc/README ]; then
        mv ${D}${datadir}/doc/README ${D}${datadir}/doc/${PN}/
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

PACKAGES = "${PN}-dbg ${PN} \
    libmysqlclient-r libmysqlclient-r-dev libmysqlclient-r-staticdev \
    libmysqlclient libmysqlclient-dev libmysqlclient-staticdev \
    libmysqld libmysqld-dev ${PN}-client ${PN}-server ${PN}-setupdb ${PN}-leftovers"
CONFFILES_${PN}-server += "${sysconfdir}/my.cnf ${sysconfdir}/my.cnf.d/server.cnf"
CONFFILES_${PN}-client += "${sysconfdir}/my.cnf.d/mysql-clients.cnf"
CONFFILES_libmysqlclient += "${sysconfdir}/my.cnf.d/client.cnf"

RPROVIDES_${PN}-dbg += "libmysqlclient-r-dbg libmysqlclient-dbg"

FILES_${PN} = " "
RDEPENDS_${PN} = "${PN}-client ${PN}-server"
ALLOW_EMPTY_${PN} = "1"

RDEPENDS_${PN}-client = "perl perl-module-getopt-long perl-module-file-temp \
    perl-module-fcntl perl-module-sys-hostname perl-module-ipc-open3 \
    perl-module-exporter"
RDEPENDS_${PN}-server = "perl perl-module-getopt-long perl-module-data-dumper \
    perl-module-file-basename perl-module-file-path perl-module-sys-hostname \
    perl-module-file-copy perl-module-file-temp perl-module-posix \
    ${PN}-client ${PN}-setupdb libdbi-perl libdbd-mysql-perl"
RDEPENDS_${PN}-leftovers = "perl perl-module-cwd perl-module-benchmark perl-module-getopt-long \
    perl-module-posix perl-module-data-dumper perl-module-sigtrap perl-module-threads \
    perl-module-threads-shared perl-module-io-socket perl-module-sys-hostname perl-module-file-copy \
    perl-module-file-spec perl-module-file-find perl-module-file-basename perl-module-file-path \
    perl-module-constant perl-module-lib perl-module-file-temp perl-module-file-spec-functions \
    perl-module-io-socket-inet perl-module-io-select bash"
RDEPENDS_${PN}-setupdb = "coreutils"

# Allow old code to link to the backward compatible library
RDEPENDS_libmysqlclient-dev = "libmysqlclient-r-dev"

FILES_libmysqlclient = "\
    ${libdir}/libmysqlclient.so.* \
    ${libdir}/libmariadb.so.* \
    ${sysconfdir}/my.cnf.d/client.cnf"
FILES_libmysqlclient-dev = " \
    ${includedir}/mysql/ \
    ${libdir}/libmysqlclient.so \
    ${libdir}/libmariadb.so \
    ${sysconfdir}/aclocal \
    ${bindir}/mysql_config"
FILES_libmysqlclient-staticdev = "\
    ${libdir}/*.a"

# Avoid warnings about ha_xtradb.so.0.0.0 and ha_innodb_plugin.so.0.0.0
# which are intentionally non-PIC on 32-bit x86 (see e.g.
# storage/xtradb/plug.in in the source)
INSANE_SKIP_libmysqlclient_append_x86 = " textrel"

FILES_libmysqlclient-r = "${libdir}/libmysqlclient_r.so.*"
FILES_libmysqlclient-r-dev = "\
    ${libdir}/libmysqlclient_r.so"
FILES_libmysqlclient-r-staticdev = "${libdir}/libmysqlclient_r.a"

FILES_libmysqld = "\
    ${libdir}/libmysqld.so.* \
    ${libdir}/libmariadbd.so.*"
FILES_libmysqld-dev = "\
    ${libdir}/libmysqld.so \
    ${libdir}/libmariadbd.so"

FILES_${PN}-client = "\
    ${bindir}/myisam_ftdump \
    ${bindir}/mysql \
    ${bindir}/mysql_client_test \
    ${bindir}/mysql_client_test_embedded \
    ${bindir}/mysql_find_rows \
    ${bindir}/mysql_fix_extensions \
    ${bindir}/mysql_waitpid \
    ${bindir}/mysqlaccess \
    ${bindir}/mysqladmin \
    ${bindir}/mysqlbug \
    ${bindir}/mysqlcheck \
    ${bindir}/mysqldump \
    ${bindir}/mysqldumpslow \
    ${bindir}/mysqlimport \
    ${bindir}/mysqlshow \
    ${bindir}/mysqlslap \
    ${bindir}/mysqltest_embedded \
    ${libexecdir}/mysqlmanager \
    ${sysconfdir}/my.cnf.d/mysql-clients.cnf"

FILES_${PN}-server = "\
    ${bindir}/comp_err \
    ${bindir}/isamchk \
    ${bindir}/isamlog \
    ${bindir}/msql2mysql \
    ${bindir}/my_print_defaults \
    ${bindir}/myisamchk \
    ${bindir}/myisamlog \
    ${bindir}/myisampack \
    ${bindir}/mysql_convert_table_format \
    ${bindir}/mysql_fix_privilege_tables \
    ${bindir}/mysql_install_db \
    ${bindir}/mysql_secure_installation \
    ${bindir}/mysql_setpermission \
    ${bindir}/mysql-systemd-start \
    ${bindir}/mysql_tzinfo_to_sql \
    ${bindir}/mysql_upgrade \
    ${bindir}/mysql_plugin \
    ${bindir}/mysql_zap \
    ${bindir}/mysqlbinlog \
    ${bindir}/mysqld_multi \
    ${bindir}/mysqld_safe \
    ${bindir}/mysqld_safe_helper \
    ${bindir}/mysqlhotcopy \
    ${bindir}/mysqltest \
    ${bindir}/ndb_delete_all \
    ${bindir}/ndb_desc \
    ${bindir}/ndb_drop_index \
    ${bindir}/ndb_drop_table \
    ${bindir}/ndb_mgm \
    ${bindir}/ndb_restore \
    ${bindir}/ndb_select_all \
    ${bindir}/ndb_select_count \
    ${bindir}/ndb_show_tables \
    ${bindir}/ndb_waiter \
    ${bindir}/pack_isam \
    ${bindir}/perror \
    ${bindir}/replace \
    ${bindir}/resolve_stack_dump \
    ${bindir}/resolveip \
    ${libexecdir}/mysqld \
    ${sbindir}/mysqld \
    ${sbindir}/ndb_cpcd \
    ${sbindir}/ndbd \
    ${sbindir}/ndb_mgmd \
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

DESCRIPTION_${PN}-leftovers = "unpackaged and probably unneeded files for ${PN}"
FILES_${PN}-leftovers = "/"
