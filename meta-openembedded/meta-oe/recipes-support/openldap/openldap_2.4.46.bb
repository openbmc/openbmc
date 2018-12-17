SUMMARY = "OpenLDAP Directory Service"
DESCRIPTION = "OpenLDAP Software is an open source implementation of the Lightweight Directory Access Protocol."
HOMEPAGE = "http://www.OpenLDAP.org/license.html"
# The OpenLDAP Public License - see the HOMEPAGE - defines
# the license.  www.openldap.org claims this is Open Source
# (see http://www.openldap.org), the license appears to be
# basically BSD.  opensource.org does not record this license
# at present (so it is apparently not OSI certified).
LICENSE = "OpenLDAP"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=25b4ead0e45182e7c2914e59ff57009f \
                    file://LICENSE;md5=153d07ef052c4a37a8fac23bc6031972 \
                    "
SECTION = "libs"

LDAP_VER = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"

SRC_URI = "ftp://ftp.openldap.org/pub/OpenLDAP/openldap-release/${BP}.tgz \
    file://openldap-m4-pthread.patch \
    file://kill-icu.patch \
    file://openldap-2.4.28-gnutls-gcrypt.patch \
    file://use-urandom.patch \
    file://initscript \
    file://slapd.service \
    file://thread_stub.patch \
    file://openldap-CVE-2015-3276.patch \
    file://remove-user-host-pwd-from-version.patch \
"

SRC_URI[md5sum] = "829016c5a9f45c51adc50073ac6f9fd7"
SRC_URI[sha256sum] = "9a90dcb86b99ae790ccab93b7585a31fbcbeec8c94bf0f7ab0ca0a87ea0c4b2d"

DEPENDS = "util-linux groff-native"

# The original top.mk used INSTALL, not INSTALL_STRIP_PROGRAM when
# installing .so and executables, this fails in cross compilation
# environments
SRC_URI += "file://install-strip.patch"

inherit autotools-brokensep update-rc.d systemd

# CV SETTINGS
# Required to work round AC_FUNC_MEMCMP which gets the wrong answer
# when cross compiling (should be in site?)
EXTRA_OECONF += "ac_cv_func_memcmp_working=yes"

# CONFIG DEFINITIONS
# The following is necessary because it cannot be determined for a
# cross compile automagically.  Select should yield fine on all OE
# systems...
EXTRA_OECONF += "--with-yielding-select=yes"
# Shared libraries are nice...
EXTRA_OECONF += "--enable-dynamic"

PACKAGECONFIG ??= "gnutls modules \
                   mdb ldap meta monitor null passwd shell proxycache dnssrv \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)} \
"
#--with-tls              with TLS/SSL support auto|openssl|gnutls [auto]
PACKAGECONFIG[gnutls] = "--with-tls=gnutls,,gnutls libgcrypt"
PACKAGECONFIG[openssl] = "--with-tls=openssl,,openssl"

PACKAGECONFIG[sasl] = "--with-cyrus-sasl,--without-cyrus-sasl,cyrus-sasl"
PACKAGECONFIG[modules] = "lt_cv_dlopen_self=yes --enable-modules,--disable-modules,libtool"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6"

# SLAPD options
#
# UNIX crypt(3) passwd support:
EXTRA_OECONF += "--enable-crypt"

# SLAPD BACKEND
#
# The backend must be set by the configuration.  This controls the
# required database.
#
# Backends="bdb dnssrv hdb ldap mdb meta monitor ndb null passwd perl relay shell sock sql"
#
# Note that multiple backends can be built.  The ldbm backend requires a
# build-time choice of database API.  The bdb backend forces this to be
# DB4.  To use the gdbm (or other) API the Berkely database module must
# be removed from the build.
md = "${libexecdir}/openldap"
#
#--enable-bdb          enable Berkeley DB backend no|yes|mod yes
# The Berkely DB is the standard choice.  This version of OpenLDAP requires
# the version 4 implementation or better.
PACKAGECONFIG[bdb] = "--enable-bdb=yes,--enable-bdb=no,db"

#--enable-dnssrv       enable dnssrv backend no|yes|mod no
PACKAGECONFIG[dnssrv] = "--enable-dnssrv=mod,--enable-dnssrv=no"

#--enable-hdb          enable Hierarchical DB backend no|yes|mod no
PACKAGECONFIG[hdb] = "--enable-hdb=yes,--enable-hdb=no,db"

#--enable-ldap         enable ldap backend no|yes|mod no
PACKAGECONFIG[ldap] = "--enable-ldap=mod,--enable-ldap=no,"

#--enable-mdb          enable mdb database backend no|yes|mod [yes]
PACKAGECONFIG[mdb] = "--enable-mdb=yes,--enable-mdb=no,"

#--enable-meta         enable metadirectory backend no|yes|mod no
PACKAGECONFIG[meta] = "--enable-meta=mod,--enable-meta=no,"

#--enable-monitor      enable monitor backend no|yes|mod yes
PACKAGECONFIG[monitor] = "--enable-monitor=mod,--enable-monitor=no,"

#--enable-ndb          enable MySQL NDB Cluster backend no|yes|mod [no]
PACKAGECONFIG[ndb] = "--enable-ndb=mod,--enable-ndb=no,"

#--enable-null         enable null backend no|yes|mod no
PACKAGECONFIG[null] = "--enable-null=mod,--enable-null=no,"

#--enable-passwd       enable passwd backend no|yes|mod no
PACKAGECONFIG[passwd] = "--enable-passwd=mod,--enable-passwd=no,"

#--enable-perl         enable perl backend no|yes|mod no
#  This requires a loadable perl dynamic library, if enabled without
#  doing something appropriate (building perl?) the build will pick
#  up the build machine perl - not good (inherit perlnative?)
PACKAGECONFIG[perl] = "--enable-perl=mod,--enable-perl=no,perl"

#--enable-relay        enable relay backend no|yes|mod [yes]
PACKAGECONFIG[relay] = "--enable-relay=mod,--enable-relay=no,"

#--enable-shell        enable shell backend no|yes|mod no
# configure: WARNING: Use of --without-threads is recommended with back-shell
PACKAGECONFIG[shell] = "--enable-shell=mod --without-threads,--enable-shell=no,"

#--enable-sock         enable sock backend no|yes|mod [no]
PACKAGECONFIG[sock] = "--enable-sock=mod,--enable-sock=no,"

#--enable-sql          enable sql backend no|yes|mod no
# sql requires some sql backend which provides sql.h, sqlite* provides
# sqlite.h (which may be compatible but hasn't been tried.)
PACKAGECONFIG[sql] = "--enable-sql=mod,--enable-sql=no,sqlite3"

#--enable-dyngroup     Dynamic Group overlay no|yes|mod no
#  This is a demo, Proxy Cache defines init_module which conflicts with the
#  same symbol in dyngroup
PACKAGECONFIG[dyngroup] = "--enable-dyngroup=mod,--enable-dyngroup=no,"

#--enable-proxycache   Proxy Cache overlay no|yes|mod no
PACKAGECONFIG[proxycache] = "--enable-proxycache=mod,--enable-proxycache=no,"
FILES_${PN}-overlay-proxycache = "${md}/pcache-*.so.*"
PACKAGES += "${PN}-overlay-proxycache"

# Append URANDOM_DEVICE='/dev/urandom' to CPPFLAGS:
# This allows tls to obtain random bits from /dev/urandom, by default
# it was disabled for cross-compiling.
CPPFLAGS_append = " -D_GNU_SOURCE -DURANDOM_DEVICE=\'/dev/urandom\' -fPIC"

LDFLAGS_append = " -pthread"

do_configure() {
    cp ${STAGING_DATADIR_NATIVE}/libtool/build-aux/ltmain.sh ${S}/build
    rm -f ${S}/libtool
    aclocal
    libtoolize --force --copy
    gnu-configize
    autoconf
    oe_runconf
}

LEAD_SONAME = "libldap-${LDAP_VER}.so.*"

# The executables go in a separate package.  This allows the
# installation of the libraries with no daemon support.
# Each module also has its own package - see above.
PACKAGES += "${PN}-slapd ${PN}-slurpd ${PN}-bin"

# Package contents - shift most standard contents to -bin
FILES_${PN} = "${libdir}/lib*.so.* ${sysconfdir}/openldap/ldap.* ${localstatedir}/${BPN}/data"
FILES_${PN}-slapd = "${sysconfdir}/init.d ${libexecdir}/slapd ${sbindir} ${localstatedir}/run ${localstatedir}/volatile/run \
    ${sysconfdir}/openldap/slapd.* ${sysconfdir}/openldap/schema \
    ${sysconfdir}/openldap/DB_CONFIG.example ${systemd_unitdir}/system/*"
FILES_${PN}-slurpd = "${libexecdir}/slurpd ${localstatedir}/openldap-slurp ${localstatedir}/run ${localstatedir}/volatile/run"
FILES_${PN}-bin = "${bindir}"
FILES_${PN}-dev = "${includedir} ${libdir}/lib*.so ${libdir}/*.la ${libdir}/*.a ${libexecdir}/openldap/*.a ${libexecdir}/openldap/*.la ${libexecdir}/openldap/*.so"
FILES_${PN}-dbg += "${libexecdir}/openldap/.debug"

do_install_append() {
    install -d ${D}${sysconfdir}/init.d
    cat ${WORKDIR}/initscript > ${D}${sysconfdir}/init.d/openldap
    chmod 755 ${D}${sysconfdir}/init.d/openldap
    # This is duplicated in /etc/openldap and is for slapd
    rm -f ${D}${localstatedir}/openldap-data/DB_CONFIG.example

    # Installing slapd under ${sbin} is more FHS and LSB compliance
    mv ${D}${libexecdir}/slapd ${D}/${sbindir}/slapd
    rmdir --ignore-fail-on-non-empty ${D}${libexecdir}
    SLAPTOOLS="slapadd slapcat slapdn slapindex slappasswd slaptest slapauth slapacl slapschema"
    cd ${D}/${sbindir}/
    rm -f ${SLAPTOOLS}
    for i in ${SLAPTOOLS}; do ln -sf slapd $i; done

    rmdir "${D}${localstatedir}/run"
    rmdir --ignore-fail-on-non-empty "${D}${localstatedir}"

    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/slapd.service ${D}${systemd_unitdir}/system/
    sed -i -e 's,@SBINDIR@,${sbindir},g' ${D}${systemd_unitdir}/system/*.service

    # Uses mdm as the database
    #  and localstatedir as data directory ...
    sed -e 's/# modulepath/modulepath/' \
        -e 's/# moduleload\s*back_bdb.*/moduleload    back_mdb/' \
        -e 's/database\s*bdb/database        mdb/' \
        -e 's%^directory\s*.*%directory   ${localstatedir}/${BPN}/data/%' \
        -i ${D}${sysconfdir}/openldap/slapd.conf

    mkdir -p ${D}${localstatedir}/${BPN}/data


}

INITSCRIPT_PACKAGES = "${PN}-slapd"
INITSCRIPT_NAME_${PN}-slapd = "openldap"
INITSCRIPT_PARAMS_${PN}-slapd = "defaults"
SYSTEMD_SERVICE_${PN}-slapd = "hostapd.service"
SYSTEMD_AUTO_ENABLE_${PN}-slapd ?= "disable"


PACKAGES_DYNAMIC += "^${PN}-backends.* ^${PN}-backend-.*"

# The modules require their .so to be dynamicaly loaded
INSANE_SKIP_${PN}-backend-dnssrv  += "dev-so"
INSANE_SKIP_${PN}-backend-ldap    += "dev-so"
INSANE_SKIP_${PN}-backend-meta    += "dev-so"
INSANE_SKIP_${PN}-backend-mdb     += "dev-so"
INSANE_SKIP_${PN}-backend-monitor += "dev-so"
INSANE_SKIP_${PN}-backend-null    += "dev-so"
INSANE_SKIP_${PN}-backend-passwd  += "dev-so"
INSANE_SKIP_${PN}-backend-shell   += "dev-so"


python populate_packages_prepend () {
    backend_dir    = d.expand('${libexecdir}/openldap')
    do_split_packages(d, backend_dir, 'back_([a-z]*)\.so$', 'openldap-backend-%s', 'OpenLDAP %s backend', prepend=True, extra_depends='', allow_links=True)
    do_split_packages(d, backend_dir, 'back_([a-z]*)\-.*\.so\..*$', 'openldap-backend-%s', 'OpenLDAP %s backend', extra_depends='', allow_links=True)

    metapkg = "${PN}-backends"
    d.setVar('ALLOW_EMPTY_' + metapkg, "1")
    d.setVar('FILES_' + metapkg, "")
    metapkg_rdepends = []
    packages = d.getVar('PACKAGES').split()
    for pkg in packages[1:]:
        if pkg.count("openldap-backend-") and not pkg in metapkg_rdepends and not pkg.count("-dev") and not pkg.count("-dbg") and not pkg.count("static") and not pkg.count("locale"):
            metapkg_rdepends.append(pkg)
    d.setVar('RDEPENDS_' + metapkg, ' '.join(metapkg_rdepends))
    d.setVar('DESCRIPTION_' + metapkg, 'OpenLDAP backends meta package')
    packages.append(metapkg)
    d.setVar('PACKAGES', ' '.join(packages))
}

BBCLASSEXTEND = "native"
