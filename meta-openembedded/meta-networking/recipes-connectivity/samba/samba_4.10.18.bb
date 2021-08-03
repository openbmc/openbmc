HOMEPAGE = "https://www.samba.org/"
SECTION = "console/network"

LICENSE = "GPL-3.0+ & LGPL-3.0+ & GPL-2.0+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://${COREBASE}/meta/files/common-licenses/LGPL-3.0;md5=bfccfe952269fff2b407dd11f2f3083b \
                    file://${COREBASE}/meta/files/common-licenses/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6 "

SAMBA_MIRROR = "http://samba.org/samba/ftp"
MIRRORS += "\
${SAMBA_MIRROR}    http://mirror.internode.on.net/pub/samba \n \
${SAMBA_MIRROR}    http://www.mirrorservice.org/sites/ftp.samba.org \n \
"

SRC_URI = "${SAMBA_MIRROR}/stable/samba-${PV}.tar.gz \
           file://smb.conf \
           file://16-do-not-check-xsltproc-manpages.patch \
           file://20-do-not-import-target-module-while-cross-compile.patch \
           file://21-add-config-option-without-valgrind.patch \
           file://netdb_defines.patch \
           file://glibc_only.patch \
           file://iconv-4.7.0.patch \
           file://dnsserver-4.7.0.patch \
           file://smb_conf-4.7.0.patch \
           file://volatiles.03_samba \
           file://0001-waf-add-support-of-cross_compile.patch \
           file://0001-lib-replace-wscript-Avoid-generating-nested-main-fun.patch \
           file://0002-util_sec.c-Move-__thread-variable-to-global-scope.patch \
           file://0001-Add-options-to-configure-the-use-of-libbsd.patch \
           file://0001-nsswitch-nsstest.c-Avoid-nss-function-conflicts-with.patch \
           file://CVE-2020-14318.patch \
           file://CVE-2020-14383.patch \
           "
SRC_URI_append_libc-musl = " \
           file://samba-pam.patch \
           file://samba-4.3.9-remove-getpwent_r.patch \
           file://cmocka-uintptr_t.patch \
           file://0001-samba-fix-musl-lib-without-innetgr.patch \
          "

SRC_URI[md5sum] = "f006a3d1876113e4a049015969d20fe6"
SRC_URI[sha256sum] = "7dcfc2aaaac565b959068788e6a43fc79ce2a03e7d523f5843f7a9fddffc7c2c"

UPSTREAM_CHECK_REGEX = "samba\-(?P<pver>4\.10(\.\d+)+).tar.gz"

inherit systemd waf-samba cpan-base perlnative update-rc.d

# CVE-2011-2411 is valnerble only on HP NonStop Servers.
CVE_CHECK_WHITELIST += "CVE-2011-2411" 

# remove default added RDEPENDS on perl
RDEPENDS_${PN}_remove = "perl"

DEPENDS += "readline virtual/libiconv zlib popt libtalloc libtdb libtevent libldb libaio libpam libtasn1 jansson"

inherit features_check
REQUIRED_DISTRO_FEATURES = "pam"

DEPENDS_append_libc-musl = " libtirpc"
CFLAGS_append_libc-musl = " -I${STAGING_INCDIR}/tirpc"
LDFLAGS_append_libc-musl = " -ltirpc"

INITSCRIPT_NAME = "samba"
INITSCRIPT_PARAMS = "start 20 3 5 . stop 20 0 1 6 ."

SYSTEMD_PACKAGES = "${PN}-base ${PN}-ad-dc winbind"
SYSTEMD_SERVICE_${PN}-base = "nmb.service smb.service"
SYSTEMD_SERVICE_${PN}-ad-dc = "${@bb.utils.contains('PACKAGECONFIG', 'ad-dc', 'samba.service', '', d)}"
SYSTEMD_SERVICE_winbind = "winbind.service"

# There are prerequisite settings to enable ad-dc, so disable the service by default.
# Reference:
# https://wiki.samba.org/index.php/Setting_up_Samba_as_an_Active_Directory_Domain_Controller
SYSTEMD_AUTO_ENABLE_${PN}-ad-dc = "disable"

#cross_compile cannot use preforked process, since fork process earlier than point subproces.popen
#to cross Popen
export WAF_NO_PREFORK="yes"

# Use krb5.  Build active domain controller.
#
PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd zeroconf', d)} \
                   acl cups ad-dc gnutls ldap mitkrb5 \
"

RDEPENDS_${PN}-ctdb-tests += "bash util-linux-getopt"

PACKAGECONFIG[acl] = "--with-acl-support,--without-acl-support,acl"
PACKAGECONFIG[fam] = "--with-fam,--without-fam,gamin"
PACKAGECONFIG[cups] = "--enable-cups,--disable-cups,cups"
PACKAGECONFIG[ldap] = "--with-ldap,--without-ldap,openldap"
PACKAGECONFIG[sasl] = ",,cyrus-sasl"
PACKAGECONFIG[systemd] = "--with-systemd,--without-systemd,systemd"
PACKAGECONFIG[dmapi] = "--with-dmapi,--without-dmapi,dmapi"
PACKAGECONFIG[zeroconf] = "--enable-avahi,--disable-avahi,avahi"
PACKAGECONFIG[valgrind] = ",--without-valgrind,valgrind,"
PACKAGECONFIG[lttng] = "--with-lttng, --without-lttng,lttng-ust"
PACKAGECONFIG[archive] = "--with-libarchive, --without-libarchive, libarchive"
PACKAGECONFIG[libunwind] = ", , libunwind"
PACKAGECONFIG[gpgme] = ",--without-gpgme,,"
PACKAGECONFIG[lmdb] = ",--without-ldb-lmdb,lmdb,"
PACKAGECONFIG[libbsd] = "--with-libbsd, --without-libbsd, libbsd"

# Building the AD (Active Directory) DC (Domain Controller) requires GnuTLS,
# And ad-dc doesn't work with mitkrb5 for versions prior to 4.7.0 according to:
# http://samba.2283325.n4.nabble.com/samba-4-6-6-Unknown-dependency-kdc-in-service-kdc-objlist-td4722096.html
# So the working combination is:
# 1) ad-dc: enable, gnutls: enable, mitkrb5: disable
# 2) ad-dc: disable, gnutls: enable/disable, mitkrb5: enable
#
# We are now at 4.7.0, so take the above with a grain of salt. We do not need to know where
# krb5kdc is unless ad-dc is enabled, but we tell configure anyhow.
#
PACKAGECONFIG[ad-dc] = "--with-experimental-mit-ad-dc,--without-ad-dc,,"
PACKAGECONFIG[gnutls] = "--enable-gnutls,--disable-gnutls,gnutls,"
PACKAGECONFIG[mitkrb5] = "--with-system-mitkrb5 --with-system-mitkdc=/usr/sbin/krb5kdc,,krb5,"

SAMBA4_IDMAP_MODULES="idmap_ad,idmap_rid,idmap_adex,idmap_hash,idmap_tdb2"
SAMBA4_PDB_MODULES="pdb_tdbsam,${@bb.utils.contains('PACKAGECONFIG', 'ldap', 'pdb_ldap,', '', d)}pdb_ads,pdb_smbpasswd,pdb_wbc_sam,pdb_samba4"
SAMBA4_AUTH_MODULES="auth_unix,auth_wbc,auth_server,auth_netlogond,auth_script,auth_samba4"
SAMBA4_MODULES="${SAMBA4_IDMAP_MODULES},${SAMBA4_PDB_MODULES},${SAMBA4_AUTH_MODULES}"

# These libraries are supposed to replace others supplied by packages, but decorate the names of
# .so files so there will not be a conflict.  This is not done consistantly, so be very careful
# when adding to this list.
#
SAMBA4_LIBS="heimdal,cmocka,NONE"

EXTRA_OECONF += "--enable-fhs \
                 --with-piddir=/run \
                 --with-sockets-dir=/run/samba \
                 --with-modulesdir=${libdir}/samba \
                 --with-lockdir=${localstatedir}/lib/samba \
                 --with-cachedir=${localstatedir}/lib/samba \
                 --disable-rpath-install \
                 --with-shared-modules=${SAMBA4_MODULES} \
                 --bundled-libraries=${SAMBA4_LIBS} \
                 ${@oe.utils.conditional('TARGET_ARCH', 'x86_64', '', '--disable-glusterfs', d)} \
                 --with-cluster-support \
                 --with-profiling-data \
                 --with-libiconv=${STAGING_DIR_HOST}${prefix} \
                 --with-pam --with-pammodulesdir=${base_libdir}/security \
                "

LDFLAGS += "-Wl,-z,relro,-z,now ${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', ' -fuse-ld=bfd ', '', d)}"

do_install_append() {
    for section in 1 5 7; do
        install -d ${D}${mandir}/man$section
        install -m 0644 ctdb/doc/*.$section ${D}${mandir}/man$section
    done
    for section in 1 5 7 8; do
        install -d ${D}${mandir}/man$section
        install -m 0644 docs/manpages/*.$section ${D}${mandir}/man$section
    done

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/bin/default/packaging/systemd/*.service ${D}${systemd_system_unitdir}/
    sed -e 's,\(ExecReload=\).*\(/kill\),\1${base_bindir}\2,' \
        -e 's,/etc/sysconfig/samba,${sysconfdir}/default/samba,' \
        -i ${D}${systemd_system_unitdir}/*.service

    if [ "${@bb.utils.contains('PACKAGECONFIG', 'ad-dc', 'yes', 'no', d)}" = "no" ]; then
        rm -f ${D}${systemd_system_unitdir}/samba.service
    fi

    install -d ${D}${sysconfdir}/tmpfiles.d
    install -m644 packaging/systemd/samba.conf.tmp ${D}${sysconfdir}/tmpfiles.d/samba.conf
    echo "d ${localstatedir}/log/samba 0755 root root -" \
        >> ${D}${sysconfdir}/tmpfiles.d/samba.conf
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 packaging/sysv/samba.init ${D}${sysconfdir}/init.d/samba
    sed -e 's,/opt/samba/bin,${sbindir},g' \
        -e 's,/opt/samba/smb.conf,${sysconfdir}/samba/smb.conf,g' \
        -e 's,/opt/samba/log,${localstatedir}/log/samba,g' \
        -e 's,/etc/init.d/samba.server,${sysconfdir}/init.d/samba,g' \
        -e 's,/usr/bin,${base_bindir},g' \
        -i ${D}${sysconfdir}/init.d/samba

    install -d ${D}${sysconfdir}/samba
    echo "127.0.0.1 localhost" > ${D}${sysconfdir}/samba/lmhosts
    install -m644 ${WORKDIR}/smb.conf ${D}${sysconfdir}/samba/smb.conf
    install -D -m 644 ${WORKDIR}/volatiles.03_samba ${D}${sysconfdir}/default/volatiles/03_samba

    install -d ${D}${sysconfdir}/default
    install -m644 packaging/systemd/samba.sysconfig ${D}${sysconfdir}/default/samba

    # the items are from ctdb/tests/run_tests.sh
    for d in onnode takeover tool eventscripts cunit simple complex; do
        testdir=${D}${datadir}/ctdb-tests/$d
        install -d $testdir
        cp ${S}/ctdb/tests/$d/*.sh $testdir
        cp -r ${S}/ctdb/tests/$d/scripts ${S}/ctdb/tests/$d/stubs $testdir || true
    done

    # fix file-rdeps qa warning
    if [ -f ${D}${bindir}/onnode ]; then
        sed -i 's:\(#!/bin/\)bash:\1sh:' ${D}${bindir}/onnode
    fi

    chmod 0750 ${D}${sysconfdir}/sudoers.d || true
    rm -rf ${D}/run ${D}${localstatedir}/run ${D}${localstatedir}/log
    
    for f in samba-gpupdate samba_upgradedns samba_spnupdate samba_kcc samba_dnsupdate; do
        if [ -f "${D}${sbindir}/$f" ]; then
            sed -i -e 's,${PYTHON},/usr/bin/env python3,g' ${D}${sbindir}/$f
        fi
    done
    if [ -f "${D}${bindir}/samba-tool" ]; then
        sed -i -e 's,${PYTHON},/usr/bin/env python3,g' ${D}${bindir}/samba-tool
    fi
    
}

PACKAGES =+ "${PN}-python3 ${PN}-pidl \
             ${PN}-dsdb-modules ${PN}-testsuite registry-tools \
             winbind \
             ${PN}-common ${PN}-base ${PN}-ad-dc ${PN}-ctdb-tests \
             smbclient ${PN}-client ${PN}-server ${PN}-test"

python samba_populate_packages() {
    def module_hook(file, pkg, pattern, format, basename):
        pn = d.getVar('PN')
        d.appendVar('RRECOMMENDS_%s-base' % pn, ' %s' % pkg)

    mlprefix = d.getVar('MLPREFIX') or ''
    pam_libdir = d.expand('${base_libdir}/security')
    pam_pkgname = mlprefix + 'pam-plugin%s'
    do_split_packages(d, pam_libdir, '^pam_(.*)\.so$', pam_pkgname, 'PAM plugin for %s', extra_depends='', prepend=True)

    libdir = d.getVar('libdir')
    do_split_packages(d, libdir, '^lib(.*)\.so\..*$', 'lib%s', 'Samba %s library', extra_depends='${PN}-common', prepend=True, allow_links=True)
    pkglibdir = '%s/samba' % libdir
    do_split_packages(d, pkglibdir, '^lib(.*)\.so$', 'lib%s', 'Samba %s library', extra_depends='${PN}-common', prepend=True)
    moduledir = '%s/samba/auth' % libdir
    do_split_packages(d, moduledir, '^(.*)\.so$', 'samba-auth-%s', 'Samba %s authentication backend', hook=module_hook, extra_depends='', prepend=True)
    moduledir = '%s/samba/pdb' % libdir
    do_split_packages(d, moduledir, '^(.*)\.so$', 'samba-pdb-%s', 'Samba %s password backend', hook=module_hook, extra_depends='', prepend=True)
}

PACKAGESPLITFUNCS_prepend = "samba_populate_packages "
PACKAGES_DYNAMIC = "samba-auth-.* samba-pdb-.*"

RDEPENDS_${PN} += "${PN}-base ${PN}-python3 ${PN}-dsdb-modules python3"
RDEPENDS_${PN}-python3 += "pytalloc python3-tdb"

FILES_${PN}-base = "${sbindir}/nmbd \
                    ${sbindir}/smbd \
                    ${sysconfdir}/init.d \
                    ${systemd_system_unitdir}/nmb.service \
                    ${systemd_system_unitdir}/smb.service"

FILES_${PN}-ad-dc = "${sbindir}/samba \
                     ${systemd_system_unitdir}/samba.service \
                     ${libdir}/krb5/plugins/kdb/samba.so \
"
RDEPENDS_${PN}-ad-dc = "krb5-kdc"

FILES_${PN}-ctdb-tests = "${bindir}/ctdb_run_tests \
                          ${bindir}/ctdb_run_cluster_tests \
                          ${sysconfdir}/ctdb/nodes \
                          ${datadir}/ctdb-tests \
                          ${datadir}/ctdb/tests \
                          ${localstatedir}/lib/ctdb \
                         "

FILES_${BPN}-common = "${sysconfdir}/default \
                       ${sysconfdir}/samba \
                       ${sysconfdir}/tmpfiles.d \
                       ${localstatedir}/lib/samba \
                       ${localstatedir}/spool/samba \
"

FILES_${PN} += "${libdir}/vfs/*.so \
                ${libdir}/charset/*.so \
                ${libdir}/*.dat \
                ${libdir}/auth/*.so \
                ${datadir}/ctdb/events/* \
"

FILES_${PN}-dsdb-modules = "${libdir}/samba/ldb"

FILES_${PN}-testsuite = "${bindir}/gentest \
                         ${bindir}/locktest \
                         ${bindir}/masktest \
                         ${bindir}/ndrdump \
                         ${bindir}/smbtorture"

FILES_registry-tools = "${bindir}/regdiff \
                        ${bindir}/regpatch \
                        ${bindir}/regshell \
                        ${bindir}/regtree"

FILES_winbind = "${sbindir}/winbindd \
                 ${bindir}/wbinfo \
                 ${bindir}/ntlm_auth \
                 ${libdir}/samba/idmap \
                 ${libdir}/samba/nss_info \
                 ${libdir}/winbind_krb5_locator.so \
                 ${libdir}/winbind-krb5-localauth.so \
                 ${sysconfdir}/init.d/winbind \
                 ${systemd_system_unitdir}/winbind.service"

FILES_${PN}-python3 = "${PYTHON_SITEPACKAGES_DIR}"

FILES_smbclient = "${bindir}/cifsdd \
                   ${bindir}/rpcclient \
                   ${bindir}/smbcacls \
                   ${bindir}/smbclient \
                   ${bindir}/smbcquotas \
                   ${bindir}/smbget \
                   ${bindir}/smbspool \
                   ${bindir}/smbtar \
                   ${bindir}/smbtree \
                   ${libdir}/samba/smbspool_krb5_wrapper"

RDEPENDS_${PN}-pidl_append = " perl"
FILES_${PN}-pidl = "${bindir}/pidl ${datadir}/perl5/Parse"

RDEPENDS_${PN}-client = "\
    smbclient \
    winbind \
    registry-tools \
    ${PN}-pidl \
    "

ALLOW_EMPTY_${PN}-client = "1"

RDEPENDS_${PN}-server = "\
    ${PN} \
    winbind \
    registry-tools \
    "

ALLOW_EMPTY_${PN}-server = "1"

RDEPENDS_${PN}-test = "\
    ${PN}-ctdb-tests \
    ${PN}-testsuite \
    "

ALLOW_EMPTY_${PN}-test = "1"
