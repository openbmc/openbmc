DESCRIPTION = "FreeRADIUS is an Internet authentication daemon, which implements the RADIUS \
protocol, as defined in RFC 2865 (and others). It allows Network Access \
Servers (NAS boxes) to perform authentication for dial-up users. There are \
also RADIUS clients available for Web servers, firewalls, Unix logins, and \
more.  Using RADIUS allows authentication and authorization for a network to \
be centralized, and minimizes the amount of re-configuration which has to be \
done when adding or deleting new users."

SUMMARY = "High-performance and highly configurable RADIUS server"
HOMEPAGE = "http://www.freeradius.org/"
SECTION = "System/Servers"
LICENSE = "GPL-2.0-only & LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=eb723b61539feef013de476e68b5c50a"
DEPENDS = "openssl-native openssl libidn libtool libpcap libtalloc"

SRC_URI = "git://github.com/FreeRADIUS/freeradius-server.git;branch=v3.2.x;lfs=0;;protocol=https \
    file://freeradius \
    file://volatiles.58_radiusd \
    file://radiusd.service \
    file://radiusd-volatiles.conf \
    file://0001-Add-autogen.sh.patch \
    file://0002-Enable-and-change-user-and-group-of-freeradius-serve.patch \
    file://0003-configure.ac-allow-cross-compilation.patch \
    file://0004-Fix-libtool-detection.patch \
    file://0005-configure.ac-add-option-for-libcap.patch \
    file://0006-Avoid-searching-host-dirs.patch \
    file://0007-rlm_python-add-PY_INC_DIR-in-search-dir.patch \
    file://0008-libtool-do-not-use-jlibtool.patch \
    file://0009-Fix-quoting-for-BUILD_WITH.patch \
    file://0010-fix-error-for-expansion-of-macro-in-thread.h.patch \
    file://0011-rlm_mschap-Use-includedir-instead-of-hardcoding-usr-.patch \
    file://0012-raddb-certs-Makefile-fix-the-existed-certificate-err.patch \
    file://0013-raddb-certs-Makefile-fix-the-occasional-verification.patch \
    file://0014-Workaround-error-with-autoconf-2.7.patch \
    file://0015-bootstrap-check-commands-of-openssl-exist.patch \
    file://0016-version.c-don-t-print-build-flags.patch \
    file://0017-Add-acinclude.m4-to-include-required-macros.patch \
"

raddbdir = "${sysconfdir}/${MLPREFIX}raddb"

SRCREV = "a7acce80f5ba2271d9aeb737a4a91a5bf8317f31"

UPSTREAM_CHECK_GITTAGREGEX = "release_(?P<pver>\d+(\_\d+)+)"

CVE_STATUS[CVE-2002-0318] = "fixed-version: The CPE in the NVD database doesn't reflect correctly the vulnerable versions."
CVE_STATUS[CVE-2011-4966] = "fixed-version: The CPE in the NVD database doesn't reflect correctly the vulnerable versions."

PARALLEL_MAKE = ""

S = "${WORKDIR}/git"

LDFLAGS:append:powerpc = " -latomic"
LDFLAGS:append:mipsarch = " -latomic"
LDFLAGS:append:armv5 = " -latomic"

EXTRA_OECONF = " --enable-strict-dependencies \
        --with-docdir=${docdir}/freeradius-${PV} \
        --with-openssl-includes=${STAGING_INCDIR} \
        --with-openssl-libraries=${STAGING_LIBDIR} \
        --with-raddbdir=${raddbdir} \
        --without-rlm_ippool \
        --without-rlm_cache_memcached \
        --without-rlm_counter \
        --without-rlm_couchbase \
        --without-rlm_dbm \
        --without-rlm_eap_tnc \
        --without-rlm_eap_ikev2 \
        --without-rlm_opendirectory \
        --without-rlm_redis \
        --without-rlm_rediswho \
        --without-rlm_cache_redis \
        --without-rlm_sql_db2 \
        --without-rlm_sql_firebird \
        --without-rlm_sql_freetds \
        --without-rlm_sql_iodbc \
        --without-rlm_sql_oracle \
        --without-rlm_sql_sybase \
        --without-rlm_sql_mongo \
        --without-rlm_sqlhpwippool \
        --without-rlm_securid \
        --without-rlm_unbound \
        --without-rlm_python \
        ac_cv_path_PERL=${bindir}/perl \
        ax_cv_cc_builtin_choose_expr=no \
        ax_cv_cc_builtin_types_compatible_p=no \
        ax_cv_cc_builtin_bswap64=no \
        ax_cv_cc_bounded_attribute=no \
"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)} \
                   pcre libcap \
                   openssl rlm-eap-fast rlm-eap-pwd \
"

PACKAGECONFIG[krb5] = "--with-rlm_krb5,--without-rlm_krb5,krb5"
PACKAGECONFIG[pam] = "--with-rlm_pam,--without-rlm_pam,libpam"
PACKAGECONFIG[libcap] = "--with-libcap,--without-libcap,libcap"
PACKAGECONFIG[ldap] = "--with-rlm_ldap,--without-rlm_ldap,openldap"
PACKAGECONFIG[mysql] = "--with-rlm_sql_mysql,--without-rlm_sql_mysql,mysql5"
PACKAGECONFIG[sqlite] = "--with-rlm_sql_sqlite,--without-rlm_sql_sqlite,sqlite3"
PACKAGECONFIG[unixodbc] = "--with-rlm_sql_unixodbc,--without-rlm_sql_unixodbc,unixodbc"
PACKAGECONFIG[postgresql] = "--with-rlm_sql_postgresql,--without-rlm_sql_postgresql,postgresql"
PACKAGECONFIG[pcre] = "--with-pcre,--without-pcre,libpcre"
PACKAGECONFIG[perl] = "--with-perl=${STAGING_BINDIR_NATIVE}/perl-native/perl --with-rlm_perl,--without-rlm_perl,perl-native perl,perl"
PACKAGECONFIG[python3] = "--with-rlm_python3 --with-rlm-python3-bin=${STAGING_BINDIR_NATIVE}/python3-native/python3 --with-rlm-python3-include-dir=${STAGING_INCDIR}/${PYTHON_DIR},--without-rlm_python3,python3-native python3"
PACKAGECONFIG[rest] = "--with-rlm_rest,--without-rlm_rest --without-rlm_json,curl json-c"
PACKAGECONFIG[ruby] = "--with-rlm_ruby,--without-rlm_ruby,ruby"
PACKAGECONFIG[openssl] = "--with-openssl, --without-openssl"
PACKAGECONFIG[rlm-eap-fast] = "--with-rlm_eap_fast, --without-rlm_eap_fast"
PACKAGECONFIG[rlm-eap-pwd] = "--with-rlm_eap_pwd, --without-rlm_eap_pwd"

inherit useradd autotools-brokensep update-rc.d systemd multilib_script multilib_header

MULTILIB_SCRIPTS = "${PN}:${sbindir}/checkrad"

# This is not a cpan or python based package, but it needs some definitions
# from cpan-base and python3-dir bbclasses for building rlm_perl and rlm_python
# correctly.
inherit cpan-base python3-dir

# The modules subdirs also need to be processed by autoreconf. Use autogen.sh
# in order to handle the subdirs correctly.
do_configure() {
    ./autogen.sh

    # the configure of rlm_perl needs this to get correct
    # mod_cflags and mod_ldflags
    if ${@bb.utils.contains('PACKAGECONFIG', 'perl', 'true', 'false', d)}; then
        export PERL5LIB="${STAGING_LIBDIR}${PERL_OWN_DIR}/perl/${@get_perl_version(d)}"
    fi

    oe_runconf

    # we don't need dhcpclient
    sed -i -e 's/dhcpclient.mk//' ${S}/src/modules/proto_dhcp/all.mk
}

INITSCRIPT_NAME = "radiusd"

SYSTEMD_SERVICE:${PN} = "radiusd.service"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --no-create-home --shell /bin/false --user-group radiusd"

do_install() {
    rm -rf ${D}
    install -d ${D}/${sysconfdir}/logrotate.d
    install -d ${D}/${sysconfdir}/pam.d
    install -d ${D}/${localstatedir}/lib/radiusd

    export LD_LIBRARY_PATH=${D}/${libdir}
    oe_runmake install R=${D} INSTALLSTRIP=""

    # remove unsupported config files
    rm -f ${D}/${raddbdir}/experimental.conf

    # remove scripts that required Perl(DBI)
    rm -rf ${D}/${bindir}/radsqlrelay

    rm -f ${D}/${sbindir}/rc.radiusd
    rm -rf ${D}/${localstatedir}/run/
    rm -rf ${D}/${localstatedir}/log/

    chown -R radiusd:radiusd ${D}/${raddbdir}
    chown -R radiusd:radiusd ${D}/${localstatedir}/lib/radiusd

    # For sysvinit
    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -d ${D}${sysconfdir}/default/volatiles
        install -m 0755 ${UNPACKDIR}/freeradius ${D}/etc/init.d/radiusd
        install -m 0644 ${UNPACKDIR}/volatiles.58_radiusd ${D}${sysconfdir}/default/volatiles/58_radiusd
    fi

    # For systemd
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${UNPACKDIR}/radiusd.service ${D}${systemd_unitdir}/system
        sed -i -e 's,@BASE_BINDIR@,${base_bindir},g' \
            -e 's,@SBINDIR@,${sbindir},g' \
            -e 's,@STATEDIR@,${localstatedir},g' \
            -e 's,@SYSCONFDIR@,${sysconfdir},g' \
            ${D}${systemd_unitdir}/system/radiusd.service

        install -d ${D}${sysconfdir}/tmpfiles.d/
        install -m 0644 ${UNPACKDIR}/radiusd-volatiles.conf ${D}${sysconfdir}/tmpfiles.d/radiusd.conf
    fi

    oe_multilib_header freeradius/autoconf.h
    oe_multilib_header freeradius/missing.h
    oe_multilib_header freeradius/radpaths.h
}

# This is only needed when we install/update on a running target.
#
pkg_postinst:${PN} () {
    if [ -z "$D" ]; then
        if command -v systemd-tmpfiles >/dev/null; then
            # create /var/log/radius, /var/run/radiusd
            systemd-tmpfiles --create ${sysconfdir}/tmpfiles.d/radiusd.conf
        elif [ -e ${sysconfdir}/init.d/populate-volatile.sh ]; then
            ${sysconfdir}/init.d/populate-volatile.sh update
        fi

        # Fix ownership for /etc/raddb/*, /var/lib/radiusd
        chown -R radiusd:radiusd ${raddbdir}
        chown -R radiusd:radiusd ${localstatedir}/lib/radiusd

        # for radiusd.service with multilib
        if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
            install -d ${sysconfdir}/sysconfig
            echo "MLPREFIX=${MLPREFIX}" > ${sysconfdir}/sysconfig/radiusd
        fi
    else
        if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
            install -d $D${sysconfdir}/sysconfig
            echo "MLPREFIX=${MLPREFIX}" > $D${sysconfdir}/sysconfig/radiusd
        fi
    fi
}

pkg_postrm:${PN} () {
    # only try to remove ${sysconfdir}/sysconfig/radiusd for systemd
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'false', 'true', d)}; then
        exit 0
    fi

    if [ -d ${sysconfdir}/raddb ]; then
        exit 0
    fi
    for variant in ${MULTILIB_GLOBAL_VARIANTS}; do
        if [ -d ${sysconfdir}/${variant}-raddb ]; then
            exit 0
        fi
    done

    rm -f ${sysconfdir}/sysconfig/radiusd
    rmdir --ignore-fail-on-non-empty ${sysconfdir}/sysconfig
}

# We really need the symlink :(
INSANE_SKIP:${PN} = "dev-so"
INSANE_SKIP:${PN}-krb5 = "dev-so"
INSANE_SKIP:${PN}-ldap = "dev-so"
INSANE_SKIP:${PN}-mysql = "dev-so"
INSANE_SKIP:${PN}-perl = "dev-so"
INSANE_SKIP:${PN}-postgresql = "dev-so"
INSANE_SKIP:${PN}-python = "dev-so"
INSANE_SKIP:${PN}-unixodbc = "dev-so"

PACKAGES =+ "${PN}-utils ${PN}-ldap ${PN}-krb5 ${PN}-perl \
    ${PN}-python ${PN}-mysql ${PN}-postgresql ${PN}-unixodbc"

FILES:${PN}-utils = "${bindir}/*"

FILES:${PN}-ldap = "${libdir}/rlm_ldap.so* \
    ${raddbdir}/mods-available/ldap \
"

FILES:${PN}-krb5 = "${libdir}/rlm_krb5.so* \
    ${raddbdir}/mods-available/krb5 \
"

FILES:${PN}-perl = "${libdir}/rlm_perl.so* \
    ${raddbdir}/mods-config/perl \
    ${raddbdir}/mods-available/perl \
"

FILES:${PN}-python = "${libdir}/rlm_python3.so* \
    ${raddbdir}/mods-config/python3 \
    ${raddbdir}/mods-available/python3 \
"

FILES:${PN}-mysql = "${libdir}/rlm_sql_mysql.so* \
    ${raddbdir}/mods-config/sql/*/mysql \
    ${raddbdir}/mods-available/sql \
"

FILES:${PN}-postgresql = "${libdir}/rlm_sql_postgresql.so* \
    ${raddbdir}/mods-config/sql/*/postgresql \
"

FILES:${PN}-unixodbc = "${libdir}/rlm_sql_unixodbc.so*"

FILES:${PN} =+ "${libdir}/rlm_*.so* ${libdir}/proto_*so*"

RDEPENDS:${PN} += "perl"
RDEPENDS:${PN}-utils = "${PN} perl"
RDEPENDS:${PN}-krb5 = "${PN}"
RDEPENDS:${PN}-ldap = "${PN}"
RDEPENDS:${PN}-mysql = "${PN}"
RDEPENDS:${PN}-perl = "${PN}"
RDEPENDS:${PN}-postgresql = "${PN}"
RDEPENDS:${PN}-python = "${PN}"
RDEPENDS:${PN}-unixodbc = "${PN}"

CLEANBROKEN = "1"
