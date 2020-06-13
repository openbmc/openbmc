SUMMARY = "PostgreSQL is a powerful, open source relational database system."
DESCRIPTION = "\
    PostgreSQL is an advanced Object-Relational database management system \
    (DBMS) that supports almost all SQL constructs (including \
    transactions, subselects and user-defined types and functions). The \
    postgresql package includes the client programs and libraries that \
    you'll need to access a PostgreSQL DBMS server.  These PostgreSQL \
    client programs are programs that directly manipulate the internal \
    structure of PostgreSQL databases on a PostgreSQL server. These client \
    programs can be located on the same machine with the PostgreSQL \
    server, or may be on a remote machine which accesses a PostgreSQL \
    server over a network connection. This package contains the docs \
    in HTML for the whole package, as well as command-line utilities for \
    managing PostgreSQL databases on a PostgreSQL server. \
    \
    If you want to manipulate a PostgreSQL database on a local or remote \
    PostgreSQL server, you need this package. You also need to install \
    this package if you're installing the postgresql-server package. \
"
HOMEPAGE = "http://www.postgresql.com"
LICENSE = "BSD-0-Clause"
DEPENDS = "libnsl2 zlib readline tzcode-native"

ARM_INSTRUCTION_SET = "arm"

SRC_URI = "http://ftp.postgresql.org/pub/source/v${PV}/${BP}.tar.bz2 \
    file://postgresql.init \
    file://postgresql-profile \
    file://postgresql.pam \
    file://postgresql-setup \
    file://postgresql.service \
"

LEAD_SONAME = "libpq.so"

# LDFLAGS for shared libraries
export LDFLAGS_SL = "${LDFLAGS}"

inherit autotools pkgconfig perlnative python3native useradd update-rc.d systemd gettext cpan-base

CFLAGS += "-I${STAGING_INCDIR}/${PYTHON_DIR} -I${STAGING_INCDIR}/tcl8.6"

SYSTEMD_SERVICE_${PN} = "postgresql.service"
SYSTEMD_AUTO_ENABLE_${PN} = "disable"

DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd-systemctl-native', '', d)}"
pkg_postinst_${PN} () {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd sysvinit', 'true', 'false', d)}; then
        if [ -n "$D" ]; then
            OPTS="--root=$D"
        fi
        systemctl $OPTS mask postgresql-server.service
    fi
}

enable_pam = "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)}"
PACKAGECONFIG ??= "${enable_pam} openssl python uuid libxml tcl nls libxml perl"
PACKAGECONFIG[pam] = "--with-pam,--without-pam,libpam,"
PACKAGECONFIG[openssl] = "--with-openssl,--without-openssl,openssl,"
PACKAGECONFIG[python] = "--with-python,--without-python,python3,python3"
PACKAGECONFIG[uuid] = "--with-uuid=e2fs,--without-uuid,util-linux,"
PACKAGECONFIG[tcl] = "--with-tcl --with-tclconfig=${STAGING_BINDIR_CROSS},--without-tcl,tcl tcl-native,"
PACKAGECONFIG[nls] = "--enable-nls,--disable-nls,,"
PACKAGECONFIG[libxml] = "--with-libxml,--without-libxml,libxml2,libxml2"
PACKAGECONFIG[perl] = "--with-perl,--without-perl,perl,perl"

EXTRA_OECONF += "--enable-thread-safety --disable-rpath \
    --datadir=${datadir}/${BPN} \
    --sysconfdir=${sysconfdir}/${BPN} \
"
EXTRA_OECONF_sh4 += "--disable-spinlocks"
EXTRA_OECONF_aarch64 += "--disable-spinlocks"

DEBUG_OPTIMIZATION_remove_mips = " -Og"
DEBUG_OPTIMIZATION_append_mips = " -O"
BUILD_OPTIMIZATION_remove_mips = " -Og"
BUILD_OPTIMIZATION_append_mips = " -O"

DEBUG_OPTIMIZATION_remove_mipsel = " -Og"
DEBUG_OPTIMIZATION_append_mipsel = " -O"
BUILD_OPTIMIZATION_remove_mipsel = " -Og"
BUILD_OPTIMIZATION_append_mipsel = " -O"

PACKAGES_DYNAMIC += "^${PN}-plperl \
    ^${PN}-pltcl \
    ^${PN}-plpython \
"

python populate_packages_prepend() {

    def fill_more(name):
        if name is None or name.strip() == "":
            return

        fpack=d.getVar('PACKAGES', False) or ""
        fpack="${PN}-" + name + " " + fpack
        d.setVar('PACKAGES', fpack)

    conf=(d.getVar('PACKAGECONFIG') or "").split()
    pack=d.getVar('PACKAGES', False) or ""
    bb.debug(1, "PACKAGECONFIG=%s" % conf)
    bb.debug(1, "PACKAGES1=%s" % pack )

    if "perl" in conf :
        fill_more("plperl")

    if "tcl" in conf:
        fill_more("pltcl")

    if "python" in conf:
        fill_more("plpython")

    pack=d.getVar('PACKAGES') or ""
    bb.debug(1, "PACKAGES2=%s" % pack)

}

# This will make native perl use target settings (for include dirs etc.)
export PERLCONFIGTARGET = "${@is_target(d)}"
export PERL_ARCHLIB = "${STAGING_LIBDIR}${PERL_OWN_DIR}/perl5/${@get_perl_version(d)}/${@get_perl_arch(d)}"

do_configure() {
    # do_configure
    autotools_do_configure

    # do_configure_append
    # workaround perl package related bugs
    sed -i -e "s:-L/usr/local/lib:-L=/usr/local/lib:g" \
        ${B}/src/Makefile.global
    LIBPNA="\${STAGING_LIBDIR_NATIVE}/perl-native"
    LIBNA="\${STAGING_LIBDIR_NATIVE}"
    BLIBNA="\${STAGING_BASE_LIBDIR_NATIVE}"
    sed -i -e "/^perl_archlibexp/s:${LIBPNA}:${STAGING_LIBDIR}:g" \
        ${B}/src/Makefile.global
    sed -i -e "/^perl_privlibexp/s:${libdir}:${STAGING_LIBDIR}:g" \
        ${B}/src/Makefile.global
    # remove the rpath, replace with correct lib path
    sed -i \
        -e "/^perl_embed_ldflags/s:-Wl,-rpath,${LIBNA}::g" \
        -e "/^perl_embed_ldflags/s:-Wl,-rpath,${BLIBNA}::g" \
        -e "/^perl_embed_ldflags/s:-Wl,-rpath-link,${LIBNA}::g" \
        -e "/^perl_embed_ldflags/s:-Wl,-rpath-link,${BLIBNA}::g" \
        -e "/^perl_embed_ldflags/s:${LIBPNA}:${STAGING_LIBDIR}:g" \
        -e "/^perl_embed_ldflags/s:${LIBNA}:${STAGING_LIBDIR}:g"  \
        -e "/^perl_embed_ldflags/s:${BLIBNA}:${STAGING_BASELIBDIR}:g" \
        -e "/^TCLSH/s:=.*:= ${bindir}/tclsh:g" \
    ${B}/src/Makefile.global

    if ${@bb.utils.contains('PACKAGECONFIG', 'perl', 'true', 'false', d)}; then
        # workaround perl package's libperl.so problem
        # we are using perlnative so this perl should have same version
        perl_version=`perl -v 2>/dev/null | \
            sed -n 's/This is perl.*v[a-z ]*\([0-9]\.[0-9][0-9.]*\).*$/\1/p'`
        if [ ! -h "${STAGING_LIBDIR}/perl/$perl_version/CORE/libperl.so" -a \
             ! -h "${STAGING_LIBDIR}/libperl.so" ]; then
            ln -sf ../../../libperl.so.5 \
                ${STAGING_LIBDIR}/perl/$perl_version/CORE/libperl.so
        fi
    fi
}

do_compile_append() {
    oe_runmake -C contrib all
}

# server needs to configure user and group
usernum = "28"
groupnum = "28"
USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "-M -g postgres -o -r -d ${localstatedir}/lib/${BPN} \
    -s /bin/sh -c 'PostgreSQL Server' -u ${usernum} postgres"
GROUPADD_PARAM_${PN} = "-g ${groupnum} -o -r postgres"

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME = "${BPN}-server"
INITSCRIPT_PARAMS = "start 64 . stop 36 0 1 2 3 4 5 6 ."

do_install_append() {
    # install contrib
    oe_runmake DESTDIR=${D} -C contrib install
    # install tutorial
    install -d -m 0755 ${D}${libdir}/${BPN}/tutorial
    install ${B}/src/tutorial/* ${D}${libdir}/${BPN}/tutorial

    # install COPYRIGHT README HISTORY
    install -d -m 0755 ${D}${docdir}/${BPN}
    for i in ${B}/COPYRIGHT ${B}/README ${B}/HISTORY ${B}/doc/KNOWN_BUGS ${B}/doc/MISSING_FEATURES ${B}/doc/README* ${B}/doc/bug.template; do
        [ -f $i ] && install $i ${D}${docdir}/${BPN}
    done

    # install dirs and server init
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/${BPN}.init ${D}${sysconfdir}/init.d/${BPN}-server
    sed -i -e "s/^PGVERSION=.*$/PGVERSION=${PV}/g" ${D}${sysconfdir}/init.d/${BPN}-server
    install -m 0755 ${WORKDIR}/${BPN}-setup ${D}${bindir}/${BPN}-setup
    install -d -m 700 ${D}${localstatedir}/lib/${BPN}/data
    install -d -m 700 ${D}${localstatedir}/lib/${BPN}/backups
    install -m 644 ${WORKDIR}/${BPN}-profile ${D}${localstatedir}/lib/${BPN}/.profile
    chown -R postgres:postgres ${D}${localstatedir}/lib/${BPN}
    # multiple server config directory
    install -d -m 700 ${D}${sysconfdir}/default/${BPN}

    if [ "${@d.getVar('enable_pam')}" = "pam" ]; then
        install -d ${D}${sysconfdir}/pam.d
        install -m 644 ${WORKDIR}/postgresql.pam ${D}${sysconfdir}/pam.d/postgresql
    fi

    # Install systemd unit files
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/postgresql.service ${D}${systemd_unitdir}/system
    sed -i -e 's,@BINDIR@,${bindir},g' \
        ${D}${systemd_unitdir}/system/postgresql.service
}

SSTATE_SCAN_FILES += "Makefile.global"
SSTATE_SCAN_FILES_remove = "*_config"

PACKAGES =+ "${PN}-client ${PN}-server-dev ${PN}-timezone \
    libecpg-compat libecpg-compat-dev \
    libecpg libecpg-dev libecpg-staticdev libecpg-doc \
    libpq libpq-dev libpq-staticdev \
    libpgtypes libpgtypes-staticdev libpgtypes-dev \
    ${PN}-contrib \
"

RPROVIDES_${PN}-dbg += "libecpg-compat-dbg \
                        libecpg-dbg \
                        libpq-dbg \
                        libpgtypes-dbg \
                        ${PN}-contrib-dbg \
                        ${PN}-pltcl-dbg \
                        ${PN}-plpython-dbg \
                        ${PN}-plperl-dbg \
                       "

FILES_${PN} += "${sysconfdir}/init.d/${BPN}-server \
    ${localstatedir}/lib/${BPN}/data ${localstatedir}/lib/${BPN}/backups \
    ${localstatedir}/lib/${BPN}/.profile ${sysconfdir}/default/${BPN} \
    ${libdir}/${BPN}/dict_snowball.so ${libdir}/${BPN}/plpgsql.so \
    ${libdir}/${BPN}/euc2004_sjis2004.so \
    ${libdir}/${BPN}/libpqwalreceiver.so \
    ${libdir}/${BPN}/*_and_*.so \
    ${@'${sysconfdir}/pam.d/postgresql' \
       if 'pam' == d.getVar('enable_pam') \
       else ''} \
"

FILES_${PN}-client = "${bindir}/clusterdb \
    ${bindir}/createdb \
    ${bindir}/createlang \
    ${bindir}/createuser \
    ${bindir}/dropdb \
    ${bindir}/droplang \
    ${bindir}/dropuser \
    ${bindir}/pg_dump \
    ${bindir}/pg_dumpall \
    ${bindir}/pg_restore \
    ${bindir}/psql \
    ${bindir}/reindexdb \
    ${bindir}/vacuumdb \
    ${bindir}/vacuumlo \
    ${datadir}/${BPN}/psqlrc.sample \
"
FILES_${PN}-client-doc = "${mandir}/man1/clusterdb.* \
    ${mandir}/man1/createdb.*   ${mandir}/man1/createlang.* \
    ${mandir}/man1/createuser.* ${mandir}/man1/dropdb.* \
    ${mandir}/man1/droplang.*   ${mandir}/man1/dropuser.* \
    ${mandir}/man1/pg_dump.*    ${mandir}/man1/pg_dumpall.* \
    ${mandir}/man1/pg_restore.* ${mandir}/man1/psql.* \
    ${mandir}/man1/reindexdb.*  ${mandir}/man1/vacuumdb.* \
    ${mandir}/man7/* \
"
FILES_${PN}-doc += "${docdir}/${BPN}/html ${libdir}/${BPN}/tutorial/ \
    ${mandir}/man1/initdb.* ${mandir}/man1/pg_controldata.* \
    ${mandir}/man1/pg_ctl.* ${mandir}/man1/pg_resetxlog.* \
    ${mandir}/man1/postgres.* ${mandir}/man1/postmaster.* \
"
FILES_${PN}-timezone = "${datadir}/${BPN}/timezone \
    ${datadir}/${BPN}/timezonesets \
"
RDEPENDS_${PN} += "${PN}-timezone"
FILES_${PN}-server-dev = "${includedir}/${BPN}/server \
                          ${libdir}/${BPN}/pgxs \
"

FILES_libecpg = "${libdir}/libecpg*${SOLIBS}"
FILES_libecpg-dev = "${libdir}/libecpg*${SOLIBSDEV} \
    ${libdir}/libpgtypes*${SOLIBSDEV} \
    ${includedir}/ecpg*.h ${includedir}/${BPN}/ecpg*.h \
    ${includedir}/pgtypes*.h ${includedir}/${BPN}/informix \
    ${includedir}/sql3types.h ${includedir}/sqlca.h \
"
FILES_libecpg-doc = "${mandir}/man1/ecpg.*"
FILES_libecpg-staticdev = "${libdir}/libecpg*.a"
SECTION_libecpg-staticdev = "devel"
RDEPENDS_libecpg-staticdev = "libecpg-dev (= ${EXTENDPKGV})"

FILES_libpq = "${libdir}/libpq*${SOLIBS}"
FILES_libpq-dev = "${libdir}/libpq*${SOLIBSDEV} \
    ${includedir} \
"
FILES_libpq-staticdev = "${libdir}/libpq*.a ${libdir}/libpgport.a"
SECTION_libpq-staticdev = "devel"
RDEPENDS_libpq-staticdev = "libpq-dev (= ${EXTENDPKGV})"

FILES_libecpg-compat = "${libdir}/libecpg_compat*${SOLIBS}"
FILES_libecpg-compat-dev = "${libdir}/libecpg_compat*${SOLIBS}"
FILES_libpgtypes = "${libdir}/libpgtypes*${SOLIBS}"
FILES_libpgtypes-staticdev = "${libdir}/libpgtypes*.a"
FILES_libpgtypes-dev = "${libdir}/libpgtypes*${SOLIBS} ${includedir}/pgtypes*.h"

FILES_${PN}-contrib = " ${bindir}/oid2name ${bindir}/pg_standby \
    ${bindir}/pgbench ${bindir}/vacuumlo \
    ${S}/contrib/spi/*.example \
    ${libdir}/${BPN}/_int.so ${libdir}/${BPN}/adminpack.so \
    ${libdir}/${BPN}/autoinc.so ${libdir}/${BPN}/auto_explain.so \
    ${libdir}/${BPN}/auth_delay.so ${libdir}/${BPN}/btree_gin.so \
    ${libdir}/${BPN}/btree_gist.so ${libdir}/${BPN}/.so \
    ${libdir}/${BPN}/chkpass.so ${libdir}/${BPN}/citext.so \
    ${libdir}/${BPN}/cube.so ${libdir}/${BPN}/dblink.so \
    ${libdir}/${BPN}/dict_int.so ${libdir}/${BPN}/dict_xsyn.so \
    ${libdir}/${BPN}/dummy_seclabel.so ${libdir}/${BPN}/earthdistance.so \
    ${libdir}/${BPN}/file_fdw.so ${libdir}/${BPN}/fuzzystrmatch.so \
    ${libdir}/${BPN}/hstore.so ${libdir}/${BPN}/insert_username.so \
    ${libdir}/${BPN}/isn.so ${libdir}/${BPN}/lo.so \
    ${libdir}/${BPN}/ltree.so ${libdir}/${BPN}/moddatetime.so \
    ${libdir}/${BPN}/pageinspect.so ${libdir}/${BPN}/pg_buffercache.so \
    ${libdir}/${BPN}/pg_freespacemap.so ${libdir}/${BPN}/pg_trgm.so \
    ${libdir}/${BPN}/pgcrypto.so ${libdir}/${BPN}/pgrowlocks.so \
    ${libdir}/${BPN}/pgstattuple.so ${libdir}/${BPN}/pg_stat_statements.so \
    ${libdir}/${BPN}/refint.so ${libdir}/${BPN}/seg.so \
    ${libdir}/${BPN}/sslinfo.so \
    ${libdir}/${BPN}/tablefunc.so \
    ${libdir}/${BPN}/test_parser.so ${libdir}/${BPN}/timetravel.so \
    ${libdir}/${BPN}/tsearch2.so ${libdir}/${BPN}/uuid-ossp.so \
    ${libdir}/${BPN}/pgxml.so ${libdir}/${BPN}/passwordcheck.so \
    ${libdir}/${BPN}/pg_upgrade_support.so ${libdir}/${BPN}/.so \
    ${libdir}/${BPN}/unaccent.so \
"
DESCRIPTION_${PN}-contrib = "The postgresql-contrib package contains \
    contributed packages that are included in the PostgreSQL distribution."

FILES_${PN}-pltcl = "${libdir}/${BPN}/pltcl.so ${bindir}/pltcl_delmod \
    ${binddir}/pltcl_listmod ${bindir}/pltcl_loadmod  \
    ${datadir}/${BPN}/unknown.pltcl"
SUMMARY_${PN}-pltcl = "The Tcl procedural language for PostgreSQL"
DESCRIPTION_${PN}-pltcl = "PostgreSQL is an advanced Object-Relational \
    database management system.  The postgresql-pltcl package contains the PL/Tcl \
    procedural language for the backend."

FILES_${PN}-plperl = "${libdir}/${BPN}/plperl.so"
SUMMARY_${PN}-plperl = "The Perl procedural language for PostgreSQL"
DESCRIPTION_${PN}-plperl = "PostgreSQL is an advanced Object-Relational \
    database management system.  The postgresql-plperl package contains the \
    PL/Perl procedural language for the backend."

# In version 8, it will be plpython.so
# In version 9, it might be plpython{2,3}.so depending on python2 or 3
FILES_${PN}-plpython = "${libdir}/${BPN}/plpython*.so"
SUMMARY_${PN}-plpython = "The Python procedural language for PostgreSQL"
DESCRIPTION_${PN}-plpython = "PostgreSQL is an advanced Object-Relational \
    database management system.  The postgresql-plpython package contains \
    the PL/Python procedural language for the backend."
