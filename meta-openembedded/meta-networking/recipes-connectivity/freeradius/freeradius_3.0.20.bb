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
LICENSE = "GPLv2 & LGPLv2+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=eb723b61539feef013de476e68b5c50a"
DEPENDS = "openssl-native openssl libidn libtool libpcap libtalloc"

SRC_URI = "git://github.com/FreeRADIUS/freeradius-server.git;branch=v3.0.x;lfs=0; \
    file://freeradius \
    file://volatiles.58_radiusd \
    file://freeradius-enble-user-in-conf.patch \
    file://freeradius-configure.ac-allow-cross-compilation.patch \
    file://freeradius-libtool-detection.patch \
    file://freeradius-configure.ac-add-option-for-libcap.patch \
    file://freeradius-avoid-searching-host-dirs.patch \
    file://freeradius-rlm_python-add-PY_INC_DIR.patch \
    file://freeradius-libtool-do-not-use-jlibtool.patch \
    file://freeradius-fix-quoting-for-BUILT_WITH.patch \
    file://freeradius-fix-error-for-expansion-of-macro.patch \
    file://0001-rlm_mschap-Use-includedir-instead-of-hardcoding-usr-.patch \
    file://0001-rlm_python3-add-PY_INC_DIR-in-search-dir.patch \
    file://radiusd.service \
    file://radiusd-volatiles.conf \
"

SRCREV = "d94c953ab9602a238433ba18533111b845fd8e9e"

PARALLEL_MAKE = ""

S = "${WORKDIR}/git"

LDFLAGS_append_powerpc = " -latomic"
LDFLAGS_append_mipsarch = " -latomic"
LDFLAGS_append_armv5 = " -latomic"

EXTRA_OECONF = " --enable-strict-dependencies \
        --with-docdir=${docdir}/freeradius-${PV} \
        --with-openssl-includes=${STAGING_INCDIR} \
        --with-openssl-libraries=${STAGING_LIBDIR} \
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
PACKAGECONFIG[rest] = "--with-rlm_rest,--without-rlm_rest,curl json-c"
PACKAGECONFIG[ruby] = "--with-rlm_ruby,--without-rlm_ruby,ruby"
PACKAGECONFIG[openssl] = "--with-openssl, --without-openssl"
PACKAGECONFIG[rlm-eap-fast] = "--with-rlm_eap_fast, --without-rlm_eap_fast"
PACKAGECONFIG[rlm-eap-pwd] = "--with-rlm_eap_pwd, --without-rlm_eap_pwd"

inherit useradd autotools-brokensep update-rc.d systemd

# This is not a cpan or python based package, but it needs some definitions
# from cpan-base and python3-dir bbclasses for building rlm_perl and rlm_python
# correctly.
inherit cpan-base python3-dir

# The modules subdirs also need to be processed by autoreconf. Use autogen.sh
# in order to handle the subdirs correctly.
do_configure () {
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

SYSTEMD_SERVICE_${PN} = "radiusd.service"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system --no-create-home --shell /bin/false --user-group radiusd"

do_install() {
    rm -rf ${D}
    mkdir -p ${D}/${sysconfdir}/logrotate.d
    mkdir -p ${D}/${sysconfdir}/pam.d
    mkdir -p ${D}/${sysconfdir}/init.d
    mkdir -p ${D}/${localstatedir}/lib/radiusd
    mkdir -p ${D}${sysconfdir}/default/volatiles

    export LD_LIBRARY_PATH=${D}/${libdir}
    oe_runmake install R=${D} INSTALLSTRIP=""

    # remove unsupported config files
    rm -f ${D}/${sysconfdir}/raddb/experimental.conf

    # remove scripts that required Perl(DBI)
    rm -rf ${D}/${bindir}/radsqlrelay

    cp -f ${WORKDIR}/freeradius ${D}/etc/init.d/radiusd
    rm -f ${D}/${sbindir}/rc.radiusd
    chmod +x ${D}/${sysconfdir}/init.d/radiusd
    rm -rf ${D}/${localstatedir}/run/
    rm -rf ${D}/${localstatedir}/log/
    install -m 0644 ${WORKDIR}/volatiles.58_radiusd  ${D}${sysconfdir}/default/volatiles/58_radiusd

    chown -R radiusd:radiusd ${D}/${sysconfdir}/raddb/
    chown -R radiusd:radiusd ${D}/${localstatedir}/lib/radiusd

    # For systemd
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${WORKDIR}/radiusd.service ${D}${systemd_unitdir}/system
        sed -i -e 's,@BASE_BINDIR@,${base_bindir},g' \
            -e 's,@SBINDIR@,${sbindir},g' \
            -e 's,@STATEDIR@,${localstatedir},g' \
            -e 's,@SYSCONFDIR@,${sysconfdir},g' \
            ${D}${systemd_unitdir}/system/radiusd.service

        install -d ${D}${sysconfdir}/tmpfiles.d/
        install -m 0644 ${WORKDIR}/radiusd-volatiles.conf ${D}${sysconfdir}/tmpfiles.d/radiusd.conf
    fi
}

# This is only needed when we install/update on a running target.
#
pkg_postinst_${PN} () {
    if [ -z "$D" ]; then
        if command -v systemd-tmpfiles >/dev/null; then
            # create /var/log/radius, /var/run/radiusd
            systemd-tmpfiles --create ${sysconfdir}/tmpfiles.d/radiusd.conf
        elif [ -e ${sysconfdir}/init.d/populate-volatile.sh ]; then
            ${sysconfdir}/init.d/populate-volatile.sh update
        fi

        # Fix ownership for /etc/raddb/*, /var/lib/radiusd
        chown -R radiusd:radiusd ${sysconfdir}/raddb
        chown -R radiusd:radiusd ${localstatedir}/lib/radiusd
    fi
}

# We really need the symlink :(
INSANE_SKIP_${PN} = "dev-so"
INSANE_SKIP_${PN}-krb5 = "dev-so"
INSANE_SKIP_${PN}-ldap = "dev-so"
INSANE_SKIP_${PN}-mysql = "dev-so"
INSANE_SKIP_${PN}-perl = "dev-so"
INSANE_SKIP_${PN}-postgresql = "dev-so"
INSANE_SKIP_${PN}-python = "dev-so"
INSANE_SKIP_${PN}-unixodbc = "dev-so"

PACKAGES =+ "${PN}-utils ${PN}-ldap ${PN}-krb5 ${PN}-perl \
    ${PN}-python ${PN}-mysql ${PN}-postgresql ${PN}-unixodbc"

FILES_${PN}-utils = "${bindir}/*"

FILES_${PN}-ldap = "${libdir}/rlm_ldap.so* \
    ${sysconfdir}/raddb/mods-available/ldap \
"

FILES_${PN}-krb5 = "${libdir}/rlm_krb5.so* \
    ${sysconfdir}/raddb/mods-available/krb5 \
"

FILES_${PN}-perl = "${libdir}/rlm_perl.so* \
    ${sysconfdir}/raddb/mods-config/perl \
    ${sysconfdir}/raddb/mods-available/perl \
"

FILES_${PN}-python = "${libdir}/rlm_python3.so* \
    ${sysconfdir}/raddb/mods-config/python3 \
    ${sysconfdir}/raddb/mods-available/python3 \
"

FILES_${PN}-mysql = "${libdir}/rlm_sql_mysql.so* \
    ${sysconfdir}/raddb/mods-config/sql/*/mysql \
    ${sysconfdir}/raddb/mods-available/sql \
"

FILES_${PN}-postgresql = "${libdir}/rlm_sql_postgresql.so* \
    ${sysconfdir}/raddb/mods-config/sql/*/postgresql \
"

FILES_${PN}-unixodbc = "${libdir}/rlm_sql_unixodbc.so*"

FILES_${PN} =+ "${libdir}/rlm_*.so* ${libdir}/proto_*so*"

RDEPENDS_${PN} += "perl"
RDEPENDS_${PN}-utils = "${PN} perl"

CLEANBROKEN = "1"
