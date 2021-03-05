SUMMARY = "A server-side, HTML-embedded scripting language"
HOMEPAGE = "http://www.php.net"
SECTION = "console/network"

LICENSE = "PHP-3.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7e571b888d585b31f9ef5edcc647fa30"

BBCLASSEXTEND = "native"
DEPENDS = "zlib bzip2 libxml2 virtual/libiconv php-native lemon-native"
DEPENDS_class-native = "zlib-native libxml2-native"

PHP_MAJOR_VERSION = "${@d.getVar('PV').split('.')[0]}"

SRC_URI = "http://php.net/distributions/php-${PV}.tar.bz2 \
           file://0001-php-don-t-use-broken-wrapper-for-mkdir.patch \
           file://debian-php-fixheader.patch \
           file://0001-configure.ac-don-t-include-build-libtool.m4.patch \
           file://0001-php.m4-don-t-unset-cache-variables.patch \
          "

SRC_URI_append_class-target = " \
            file://iconv.patch \
            file://imap-fix-autofoo.patch \
            file://php_exec_native.patch \
            file://php-fpm.conf \
            file://php-fpm-apache.conf \
            file://70_mod_php${PHP_MAJOR_VERSION}.conf \
            file://php-fpm.service \
            file://pear-makefile.patch \
            file://phar-makefile.patch \
            file://0001-opcache-config.m4-enable-opcache.patch \
            file://xfail_two_bug_tests.patch \
            file://CVE-2020-7070.patch \
            file://CVE-2020-7069.patch \
          "

S = "${WORKDIR}/php-${PV}"
SRC_URI[md5sum] = "e68a66c54b080d108831f6dc2e1e403d"
SRC_URI[sha256sum] = "2e270958a4216480da7886743438ccc92b6acf32ea96fefda88d07e0a5095deb"

inherit autotools pkgconfig python3native gettext

# phpize is not scanned for absolute paths by default (but php-config is).
#
SSTATE_SCAN_FILES += "phpize"
SSTATE_SCAN_FILES += "build-defs.h"

PHP_LIBDIR = "${libdir}/php${PHP_MAJOR_VERSION}"

# Common EXTRA_OECONF
COMMON_EXTRA_OECONF = "--enable-sockets \
                       --enable-pcntl \
                       --enable-shared \
                       --disable-rpath \
                       --with-pic \
                       --libdir=${PHP_LIBDIR} \
"
EXTRA_OECONF = "--enable-mbstring \
                --enable-fpm \
                --with-libdir=${baselib} \
                --with-gettext=${STAGING_LIBDIR}/.. \
                --with-zlib=${STAGING_LIBDIR}/.. \
                --with-iconv=${STAGING_LIBDIR}/.. \
                --with-bz2=${STAGING_DIR_TARGET}${exec_prefix} \
                --with-config-file-path=${sysconfdir}/php/apache2-php${PHP_MAJOR_VERSION} \
                ${@oe.utils.conditional('SITEINFO_ENDIANNESS', 'le', 'ac_cv_c_bigendian_php=no', 'ac_cv_c_bigendian_php=yes', d)} \
                ${@bb.utils.contains('PACKAGECONFIG', 'pam', '', 'ac_cv_lib_pam_pam_start=no', d)} \
                ${COMMON_EXTRA_OECONF} \
"

EXTRA_OECONF_append_riscv64 = " --with-pcre-jit=no"
EXTRA_OECONF_append_riscv32 = " --with-pcre-jit=no"

CACHED_CONFIGUREVARS += "ac_cv_func_dlopen=no ac_cv_lib_dl_dlopen=yes"

EXTRA_OECONF_class-native = " \
                --with-zlib=${STAGING_LIBDIR_NATIVE}/.. \
                --without-iconv \
                ${COMMON_EXTRA_OECONF} \
"

PACKAGECONFIG ??= "mysql sqlite3 imap opcache openssl \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6 pam', d)} \
"
PACKAGECONFIG_class-native = ""

PACKAGECONFIG[zip] = "--with-zip --with-zlib-dir=${STAGING_EXECPREFIXDIR},,libzip"

PACKAGECONFIG[mysql] = "--with-mysqli=mysqlnd \
                        --with-pdo-mysql=mysqlnd \
                        ,--without-mysqli --without-pdo-mysql \
                        ,mysql5"

PACKAGECONFIG[sqlite3] = "--with-sqlite3=${STAGING_LIBDIR}/.. \
                          --with-pdo-sqlite=${STAGING_LIBDIR}/.. \
                          ,--without-sqlite3 --without-pdo-sqlite \
                          ,sqlite3"
PACKAGECONFIG[pgsql] = "--with-pgsql=${STAGING_DIR_TARGET}${exec_prefix},--without-pgsql,postgresql"
PACKAGECONFIG[soap] = "--enable-soap, --disable-soap, libxml2"
PACKAGECONFIG[apache2] = "--with-apxs2=${STAGING_BINDIR_CROSS}/apxs,,apache2-native apache2"
PACKAGECONFIG[pam] = ",,libpam"
PACKAGECONFIG[imap] = "--with-imap=${STAGING_DIR_HOST} \
                       --with-imap-ssl=${STAGING_DIR_HOST} \
                       ,--without-imap --without-imap-ssl \
                       ,uw-imap"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
PACKAGECONFIG[opcache] = "--enable-opcache,--disable-opcache"
PACKAGECONFIG[openssl] = "--with-openssl,--without-openssl,openssl"
PACKAGECONFIG[valgrind] = "--with-valgrind=${STAGING_DIR_TARGET}/usr,--with-valgrind=no,valgrind"
PACKAGECONFIG[mbregex] = "--enable-mbregex, --disable-mbregex, oniguruma"
PACKAGECONFIG[mbstring] = "--enable-mbstring,,"

export PHP_NATIVE_DIR = "${STAGING_BINDIR_NATIVE}"
export PHP_PEAR_PHP_BIN = "${STAGING_BINDIR_NATIVE}/php"
CFLAGS += " -D_GNU_SOURCE -g -DPTYS_ARE_GETPT -DPTYS_ARE_SEARCHED -I${STAGING_INCDIR}/apache2"

# Adding these flags enables dynamic library support, which is disabled by
# default when cross compiling
# See https://bugs.php.net/bug.php?id=60109
CFLAGS += " -DHAVE_LIBDL "
LDFLAGS += " -ldl "

EXTRA_OEMAKE = "INSTALL_ROOT=${D}"

acpaths = ""

do_configure_prepend () {
    rm -f ${S}/build/libtool.m4 ${S}/ltmain.sh ${S}/aclocal.m4
    find ${S} -name config.m4 | xargs -n1 sed -i 's!APXS_HTTPD=.*!APXS_HTTPD=${STAGING_SBINDIR_NATIVE}/httpd!'
}

do_configure_append() {
    # No, libtool, we really don't want rpath set...
    sed -i 's|^hardcode_libdir_flag_spec=.*|hardcode_libdir_flag_spec=""|g' ${HOST_SYS}-libtool
    sed -i 's|^runpath_var=LD_RUN_PATH|runpath_var=DIE_RPATH_DIE|g' ${HOST_SYS}-libtool
}

do_install_append_class-native() {
    rm -rf ${D}/${PHP_LIBDIR}/php/.registry
    rm -rf ${D}/${PHP_LIBDIR}/php/.channels
    rm -rf ${D}/${PHP_LIBDIR}/php/.[a-z]*
}

do_install_prepend() {
    cat ${ACLOCALDIR}/libtool.m4 ${ACLOCALDIR}/lt~obsolete.m4 ${ACLOCALDIR}/ltoptions.m4 \
        ${ACLOCALDIR}/ltsugar.m4 ${ACLOCALDIR}/ltversion.m4 > ${S}/build/libtool.m4
}

do_install_prepend_class-target() {
    if ${@bb.utils.contains('PACKAGECONFIG', 'apache2', 'true', 'false', d)}; then
        # Install dummy config file so apxs doesn't fail
        install -d ${D}${sysconfdir}/apache2
        printf "\nLoadModule dummy_module modules/mod_dummy.so\n" > ${D}${sysconfdir}/apache2/httpd.conf
    fi
}

# fixme
do_install_append_class-target() {
    install -d ${D}${sysconfdir}/
    rm -rf ${D}/.registry
    rm -rf ${D}/.channels
    rm -rf ${D}/.[a-z]*
    rm -rf ${D}/var
    rm -f  ${D}/${sysconfdir}/php-fpm.conf.default
    install -m 0644 ${WORKDIR}/php-fpm.conf ${D}/${sysconfdir}/php-fpm.conf
    install -d ${D}/${sysconfdir}/apache2/conf.d
    install -m 0644 ${WORKDIR}/php-fpm-apache.conf ${D}/${sysconfdir}/apache2/conf.d/php-fpm.conf
    install -d ${D}${sysconfdir}/init.d
    sed -i 's:=/usr/sbin:=${sbindir}:g' ${B}/sapi/fpm/init.d.php-fpm
    sed -i 's:=/etc:=${sysconfdir}:g' ${B}/sapi/fpm/init.d.php-fpm
    sed -i 's:=/var:=${localstatedir}:g' ${B}/sapi/fpm/init.d.php-fpm
    install -m 0755 ${B}/sapi/fpm/init.d.php-fpm ${D}${sysconfdir}/init.d/php-fpm
    install -m 0644 ${WORKDIR}/php-fpm-apache.conf ${D}/${sysconfdir}/apache2/conf.d/php-fpm.conf

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)};then
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${WORKDIR}/php-fpm.service ${D}${systemd_unitdir}/system/
        sed -i -e 's,@SYSCONFDIR@,${sysconfdir},g' \
            -e 's,@LOCALSTATEDIR@,${localstatedir},g' \
            ${D}${systemd_unitdir}/system/php-fpm.service
    fi

    if ${@bb.utils.contains('PACKAGECONFIG', 'apache2', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/apache2/modules.d
        install -d ${D}${sysconfdir}/php/apache2-php${PHP_MAJOR_VERSION}
        install -m 644  ${WORKDIR}/70_mod_php${PHP_MAJOR_VERSION}.conf ${D}${sysconfdir}/apache2/modules.d
        sed -i s,lib/,${libexecdir}/, ${D}${sysconfdir}/apache2/modules.d/70_mod_php${PHP_MAJOR_VERSION}.conf
        cat ${S}/php.ini-production | \
            sed -e 's,extension_dir = \"\./\",extension_dir = \"/usr/lib/extensions\",' \
            > ${D}${sysconfdir}/php/apache2-php${PHP_MAJOR_VERSION}/php.ini
        rm -f ${D}${sysconfdir}/apache2/httpd.conf*
    fi
}

SYSROOT_PREPROCESS_FUNCS += "php_sysroot_preprocess"

php_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${bindir_crossscripts}/
    install -m 755 ${D}${bindir}/phpize ${SYSROOT_DESTDIR}${bindir_crossscripts}/
    install -m 755 ${D}${bindir}/php-config ${SYSROOT_DESTDIR}${bindir_crossscripts}/

    sed -i 's!eval echo /!eval echo ${STAGING_DIR_HOST}/!' ${SYSROOT_DESTDIR}${bindir_crossscripts}/phpize
    sed -i 's!^include_dir=.*!include_dir=${STAGING_INCDIR}/php!' ${SYSROOT_DESTDIR}${bindir_crossscripts}/php-config
}

MODPHP_PACKAGE = "${@bb.utils.contains('PACKAGECONFIG', 'apache2', '${PN}-modphp', '', d)}"

PACKAGES = "${PN}-dbg ${PN}-cli ${PN}-phpdbg ${PN}-cgi ${PN}-fpm ${PN}-fpm-apache2 ${PN}-pear ${PN}-phar ${MODPHP_PACKAGE} ${PN}-dev ${PN}-staticdev ${PN}-doc ${PN}-opcache ${PN}"

RDEPENDS_${PN} += "libgcc"
RDEPENDS_${PN}-pear = "${PN}"
RDEPENDS_${PN}-phar = "${PN}-cli"
RDEPENDS_${PN}-cli = "${PN}"
RDEPENDS_${PN}-modphp = "${PN} apache2"
RDEPENDS_${PN}-opcache = "${PN}"

INITSCRIPT_PACKAGES = "${PN}-fpm"
inherit update-rc.d

FILES_${PN}-dbg =+ "${bindir}/.debug \
                    ${libexecdir}/apache2/modules/.debug"
FILES_${PN}-doc += "${PHP_LIBDIR}/php/doc"
FILES_${PN}-cli = "${bindir}/php"
FILES_${PN}-phpdbg = "${bindir}/phpdbg"
FILES_${PN}-phar = "${bindir}/phar*"
FILES_${PN}-cgi = "${bindir}/php-cgi"
FILES_${PN}-fpm = "${sbindir}/php-fpm ${sysconfdir}/php-fpm.conf ${datadir}/fpm ${sysconfdir}/init.d/php-fpm ${systemd_unitdir}/system/php-fpm.service ${sysconfdir}/php-fpm.d/www.conf.default"
FILES_${PN}-fpm-apache2 = "${sysconfdir}/apache2/conf.d/php-fpm.conf"
CONFFILES_${PN}-fpm = "${sysconfdir}/php-fpm.conf"
CONFFILES_${PN}-fpm-apache2 = "${sysconfdir}/apache2/conf.d/php-fpm.conf"
INITSCRIPT_NAME_${PN}-fpm = "php-fpm"
INITSCRIPT_PARAMS_${PN}-fpm = "defaults 60"
FILES_${PN}-pear = "${bindir}/pear* ${bindir}/pecl ${PHP_LIBDIR}/php/PEAR \
                ${PHP_LIBDIR}/php/PEAR*.php ${PHP_LIBDIR}/php/System.php \
                ${PHP_LIBDIR}/php/peclcmd.php ${PHP_LIBDIR}/php/pearcmd.php \
                ${PHP_LIBDIR}/php/.channels ${PHP_LIBDIR}/php/.channels/.alias \
                ${PHP_LIBDIR}/php/.registry ${PHP_LIBDIR}/php/Archive/Tar.php \
                ${PHP_LIBDIR}/php/Console/Getopt.php ${PHP_LIBDIR}/php/OS/Guess.php \
                ${PHP_LIBDIR}/php/data/PEAR \
                ${sysconfdir}/pear.conf"
FILES_${PN}-dev = "${includedir}/php ${PHP_LIBDIR}/build ${bindir}/phpize \
                ${bindir}/php-config ${PHP_LIBDIR}/php/.depdb \
                ${PHP_LIBDIR}/php/.depdblock ${PHP_LIBDIR}/php/.filemap \
                ${PHP_LIBDIR}/php/.lock ${PHP_LIBDIR}/php/test"
FILES_${PN}-staticdev += "${PHP_LIBDIR}/extensions/*/*.a"
FILES_${PN}-opcache = "${PHP_LIBDIR}/extensions/*/opcache${SOLIBSDEV}"
FILES_${PN} = "${PHP_LIBDIR}/php"
FILES_${PN} += "${bindir} ${libexecdir}/apache2"

SUMMARY_${PN}-modphp = "PHP module for the Apache HTTP server"
FILES_${PN}-modphp = "${libdir}/apache2 ${sysconfdir}"

MODPHP_OLDPACKAGE = "${@bb.utils.contains('PACKAGECONFIG', 'apache2', 'modphp', '', d)}"
RPROVIDES_${PN}-modphp = "${MODPHP_OLDPACKAGE}"
RREPLACES_${PN}-modphp = "${MODPHP_OLDPACKAGE}"
RCONFLICTS_${PN}-modphp = "${MODPHP_OLDPACKAGE}"

do_install_append_class-native() {
    create_wrapper ${D}${bindir}/php \
        PHP_PEAR_SYSCONF_DIR=${sysconfdir}/
}


# Fails to build with thumb-1 (qemuarm)
# | {standard input}: Assembler messages:
# | {standard input}:3719: Error: selected processor does not support Thumb mode `smull r0,r2,r9,r3'
# | {standard input}:3720: Error: unshifted register required -- `sub r2,r2,r0,asr#31'
# | {standard input}:3796: Error: selected processor does not support Thumb mode `smull r0,r2,r3,r3'
# | {standard input}:3797: Error: unshifted register required -- `sub r2,r2,r0,asr#31'
# | make: *** [ext/standard/math.lo] Error 1
ARM_INSTRUCTION_SET = "arm"
