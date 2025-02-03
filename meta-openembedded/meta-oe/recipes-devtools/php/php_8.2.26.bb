SUMMARY = "A server-side, HTML-embedded scripting language"
HOMEPAGE = "http://www.php.net"
SECTION = "console/network"

LICENSE = "PHP-3.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fd469cce1a919f0cc95bab7afb28d19d"

BBCLASSEXTEND = "native"
DEPENDS = "zlib bzip2 libxml2 virtual/libiconv php-native"
DEPENDS:append:libc-musl = " libucontext"
DEPENDS:class-native = "zlib-native libxml2-native"

PHP_MAJOR_VERSION = "${@d.getVar('PV').split('.')[0]}"

SRC_URI = "http://php.net/distributions/php-${PV}.tar.bz2 \
           file://0002-build-php.m4-don-t-unset-cache-variables.patch \
           file://0003-php-remove-host-specific-info-from-header-file.patch \
           file://0004-configure.ac-don-t-include-build-libtool.m4.patch \
           file://0006-ext-phar-Makefile.frag-Fix-phar-packaging.patch \
           file://0009-php-don-t-use-broken-wrapper-for-mkdir.patch \
           file://0010-iconv-fix-detection.patch \
           file://0001-Change-whether-to-inline-XXH3_hashLong_withSecret-to.patch \
          "

SRC_URI:append:class-target = " \
            file://0001-ext-opcache-config.m4-enable-opcache.patch \
            file://0005-pear-fix-Makefile.frag-for-Yocto.patch \
            file://0007-sapi-cli-config.m4-fix-build-directory.patch \
            file://0008-ext-imap-config.m4-fix-include-paths.patch \
            file://php-fpm.conf \
            file://php-fpm-apache.conf \
            file://70_mod_php${PHP_MAJOR_VERSION}.conf \
            file://php-fpm.service \
          "

S = "${WORKDIR}/php-${PV}"
SRC_URI[sha256sum] = "be57c347d451c905bcb4336832a864d9928dd0e20989b872705fea0ba6476c6b"

CVE_STATUS_GROUPS += "CVE_STATUS_PHP"
CVE_STATUS_PHP[status] = "fixed-version: The name of this product is exactly the same as github.com/emlog/emlog. CVE can be safely ignored."
CVE_STATUS_PHP = " \
    CVE-2007-2728 \
    CVE-2007-3205 \
    CVE-2007-4596 \
"
CVE_STATUS[CVE-2022-4900] = "cpe-incorrect: The current version (8.2.20) is not affected."

inherit autotools pkgconfig python3native gettext multilib_header multilib_script systemd

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

EXTRA_OECONF:append:riscv64 = " --with-pcre-jit=no"
EXTRA_OECONF:append:riscv32 = " --with-pcre-jit=no"
# Needs fibers assembly implemented for rv32
# for example rv64 implementation is below
# see https://github.com/php/php-src/commit/70b02d75f2abe3a292d49c4a4e9e4f850c2fee68
EXTRA_OECONF:append:riscv32:libc-musl = " --disable-fiber-asm"

CACHED_CONFIGUREVARS += "ac_cv_func_dlopen=no ac_cv_lib_dl_dlopen=yes"

EXTRA_OECONF:class-native = " \
                --with-zlib=${STAGING_LIBDIR_NATIVE}/.. \
                --without-iconv \
                ${COMMON_EXTRA_OECONF} \
"

PACKAGECONFIG ??= "mysql sqlite3 imap opcache openssl \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6 pam', d)} \
"
PACKAGECONFIG:class-native = ""

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

export HOSTCC = "${BUILD_CC}"
export PHP_NATIVE_DIR = "${STAGING_BINDIR_NATIVE}"
export PHP_PEAR_PHP_BIN = "${STAGING_BINDIR_NATIVE}/php"
CFLAGS += " -D_GNU_SOURCE -D_LARGEFILE64_SOURCE -g -DPTYS_ARE_GETPT -DPTYS_ARE_SEARCHED -I${STAGING_INCDIR}/apache2"

# Adding these flags enables dynamic library support, which is disabled by
# default when cross compiling
# See https://bugs.php.net/bug.php?id=60109
CFLAGS += " -DHAVE_LIBDL "
LDFLAGS += " -ldl "
LDFLAGS:append:libc-musl = " -lucontext "
LDFLAGS:append:riscv64 = " -latomic"

EXTRA_OEMAKE = "INSTALL_ROOT=${D}"

acpaths = ""

do_configure:prepend () {
    rm -f ${S}/build/libtool.m4 ${S}/ltmain.sh ${S}/aclocal.m4
    find ${S} -name config.m4 | xargs -n1 sed -i 's!APXS_HTTPD=.*!APXS_HTTPD=${STAGING_SBINDIR_NATIVE}/httpd!'
}

do_configure:append() {
    # No, libtool, we really don't want rpath set...
    sed -i 's|^hardcode_libdir_flag_spec=.*|hardcode_libdir_flag_spec=""|g' libtool
    sed -i 's|^runpath_var=LD_RUN_PATH|runpath_var=DIE_RPATH_DIE|g' libtool
    sed -i -e's@${RECIPE_SYSROOT}@@g' \
        -e's@-ffile-prefix-map=[^ ]*[ ]*@@g' \
        -e's@-fdebug-prefix-map=[^ ]*[ ]*@@g' \
        -e's@-ffile-prefix-map=[^ ]*[ ]*@@g' \
        -e's@-fmacro-prefix-map=[^ ]*[ ]*@@g' \
        ${B}/main/build-defs.h \
        ${B}/scripts/php-config
}

do_install:append:class-native() {
    rm -rf ${D}/${PHP_LIBDIR}/php/.registry
    rm -rf ${D}/${PHP_LIBDIR}/php/.channels
    rm -rf ${D}/${PHP_LIBDIR}/php/.[a-z]*
}

do_install:prepend() {
    cat ${STAGING_DATADIR}/aclocal/libtool.m4 \
    ${STAGING_DATADIR}/aclocal/lt~obsolete.m4 \
    ${STAGING_DATADIR}/aclocal/ltoptions.m4 \
    ${STAGING_DATADIR}/aclocal/ltsugar.m4 \
    ${STAGING_DATADIR}/aclocal/ltversion.m4 > ${S}/build/libtool.m4
}

do_install:prepend:class-target() {
    if ${@bb.utils.contains('PACKAGECONFIG', 'apache2', 'true', 'false', d)}; then
        # Install dummy config file so apxs doesn't fail
        install -d ${D}${sysconfdir}/apache2
        printf "\nLoadModule dummy_module modules/mod_dummy.so\n" > ${D}${sysconfdir}/apache2/httpd.conf
    fi
}

# fixme
do_install:append:class-target() {
    install -d ${D}${sysconfdir}/
    rm -rf ${D}/.registry
    rm -rf ${D}/.channels
    rm -rf ${D}/.[a-z]*
    rm -rf ${D}/var
    rm -f  ${D}/${sysconfdir}/php-fpm.conf.default
    install -m 0644 ${UNPACKDIR}/php-fpm.conf ${D}/${sysconfdir}/php-fpm.conf
    install -d ${D}/${sysconfdir}/apache2/conf.d
    install -m 0644 ${UNPACKDIR}/php-fpm-apache.conf ${D}/${sysconfdir}/apache2/conf.d/php-fpm.conf
    install -d ${D}${sysconfdir}/init.d
    sed -i 's:=/usr/sbin:=${sbindir}:g' ${B}/sapi/fpm/init.d.php-fpm
    sed -i 's:=/etc:=${sysconfdir}:g' ${B}/sapi/fpm/init.d.php-fpm
    sed -i 's:=/var:=${localstatedir}:g' ${B}/sapi/fpm/init.d.php-fpm
    install -m 0755 ${B}/sapi/fpm/init.d.php-fpm ${D}${sysconfdir}/init.d/php-fpm
    install -m 0644 ${UNPACKDIR}/php-fpm-apache.conf ${D}/${sysconfdir}/apache2/conf.d/php-fpm.conf

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)};then
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${UNPACKDIR}/php-fpm.service ${D}${systemd_system_unitdir}/php-fpm.service
        sed -i -e 's,@LOCALSTATEDIR@,${localstatedir},g' ${D}${systemd_system_unitdir}/php-fpm.service
        sed -i -e 's,@SBINDIR@,${sbindir},g' ${D}${systemd_system_unitdir}/php-fpm.service
        sed -i -e 's,@BINDIR@,${bindir},g' ${D}${systemd_system_unitdir}/php-fpm.service
    fi

    if ${@bb.utils.contains('PACKAGECONFIG', 'apache2', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/apache2/modules.d
        install -d ${D}${sysconfdir}/php/apache2-php${PHP_MAJOR_VERSION}
        install -m 644  ${UNPACKDIR}/70_mod_php${PHP_MAJOR_VERSION}.conf ${D}${sysconfdir}/apache2/modules.d
        sed -i s,lib/,${libexecdir}/, ${D}${sysconfdir}/apache2/modules.d/70_mod_php${PHP_MAJOR_VERSION}.conf
        cat ${S}/php.ini-production | \
            sed -e 's,extension_dir = \"\./\",extension_dir = \"/usr/lib/extensions\",' \
            > ${D}${sysconfdir}/php/apache2-php${PHP_MAJOR_VERSION}/php.ini
        rm -f ${D}${sysconfdir}/apache2/httpd.conf*
    fi
}

MULTILIB_SCRIPTS += "${PN}:${bindir}/php-config \
                     ${PN}:${bindir}/phpize \
"

do_install:append () {
        oe_multilib_header php/main/build-defs.h php/main/php_config.h
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

RDEPENDS:${PN} += "libgcc"
RDEPENDS:${PN}-pear = "${PN}"
RDEPENDS:${PN}-phar = "${PN}-cli"
RDEPENDS:${PN}-cli = "${PN}"
RDEPENDS:${PN}-modphp = "${PN} apache2"
RDEPENDS:${PN}-opcache = "${PN}"

ALLOW_EMPTY:${PN} = "1"

INITSCRIPT_PACKAGES = "${PN}-fpm"
inherit update-rc.d

# WARNING: lib32-php-8.0.12-r0 do_package_qa: QA Issue: lib32-php: ELF binary /usr/libexec/apache2/modules/libphp.so has relocations in .text [textrel]
#WARNING: lib32-php-8.0.12-r0 do_package_qa: QA Issue: lib32-php-opcache: ELF binary /usr/lib/php8/extensions/no-debug-zts-20200930/opcache.so has relocations in .text [textrel]
INSANE_SKIP:${PN}:append:x86 = " textrel"
INSANE_SKIP:${PN}-opcache:append:x86 = " textrel"

FILES:${PN}-dbg =+ "${bindir}/.debug \
                    ${libexecdir}/apache2/modules/.debug"
FILES:${PN}-doc += "${PHP_LIBDIR}/php/doc"
FILES:${PN}-cli = "${bindir}/php"
FILES:${PN}-phpdbg = "${bindir}/phpdbg"
FILES:${PN}-phar = "${bindir}/phar*"
FILES:${PN}-cgi = "${bindir}/php-cgi"
FILES:${PN}-fpm = "${sbindir}/php-fpm ${sysconfdir}/php-fpm.conf ${datadir}/fpm ${sysconfdir}/init.d/php-fpm ${sysconfdir}/php-fpm.d/www.conf.default"
FILES:${PN}-fpm-apache2 = "${sysconfdir}/apache2/conf.d/php-fpm.conf"
CONFFILES:${PN}-fpm = "${sysconfdir}/php-fpm.conf"
CONFFILES:${PN}-fpm-apache2 = "${sysconfdir}/apache2/conf.d/php-fpm.conf"
INITSCRIPT_NAME:${PN}-fpm = "php-fpm"
INITSCRIPT_PARAMS:${PN}-fpm = "defaults 60"
FILES:${PN}-pear = "${bindir}/pear* ${bindir}/pecl ${PHP_LIBDIR}/php/PEAR \
                ${PHP_LIBDIR}/php/PEAR*.php ${PHP_LIBDIR}/php/System.php \
                ${PHP_LIBDIR}/php/peclcmd.php ${PHP_LIBDIR}/php/pearcmd.php \
                ${PHP_LIBDIR}/php/.channels ${PHP_LIBDIR}/php/.channels/.alias \
                ${PHP_LIBDIR}/php/.registry ${PHP_LIBDIR}/php/Archive/Tar.php \
                ${PHP_LIBDIR}/php/Console/Getopt.php ${PHP_LIBDIR}/php/OS/Guess.php \
                ${PHP_LIBDIR}/php/data/PEAR \
                ${sysconfdir}/pear.conf"
FILES:${PN}-dev = "${includedir}/php ${PHP_LIBDIR}/build ${bindir}/phpize \
                ${bindir}/php-config ${PHP_LIBDIR}/php/.depdb \
                ${PHP_LIBDIR}/php/.depdblock ${PHP_LIBDIR}/php/.filemap \
                ${PHP_LIBDIR}/php/.lock ${PHP_LIBDIR}/php/test"
FILES:${PN}-staticdev += "${PHP_LIBDIR}/extensions/*/*.a"
FILES:${PN}-opcache = "${PHP_LIBDIR}/extensions/*/opcache${SOLIBSDEV}"
FILES:${PN} = "${PHP_LIBDIR}/php"
FILES:${PN} += "${bindir} ${libexecdir}/apache2"

SUMMARY:${PN}-modphp = "PHP module for the Apache HTTP server"
FILES:${PN}-modphp = "${libdir}/apache2 ${sysconfdir}"

MODPHP_OLDPACKAGE = "${@bb.utils.contains('PACKAGECONFIG', 'apache2', 'modphp', '', d)}"
RPROVIDES:${PN}-modphp = "${MODPHP_OLDPACKAGE}"
RREPLACES:${PN}-modphp = "${MODPHP_OLDPACKAGE}"
RCONFLICTS:${PN}-modphp = "${MODPHP_OLDPACKAGE}"

SYSTEMD_SERVICE:${PN}-fpm = "php-fpm.service"
SYSTEMD_PACKAGES += "${PN}-fpm"

do_install:append:class-native() {
    create_wrapper ${D}${bindir}/php \
        PHP_PEAR_SYSCONF_DIR=${sysconfdir}/
}
