HOMEPAGE = "https://www.samba.org/"
SECTION = "console/network"

LICENSE = "GPL-3.0-or-later & LGPL-3.0-or-later & GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://${COREBASE}/meta/files/common-licenses/LGPL-3.0-or-later;md5=c51d3eef3be114124d11349ca0d7e117 \
                    file://${COREBASE}/meta/files/common-licenses/GPL-2.0-or-later;md5=fed54355545ffd980b814dab4a3b312c"

SAMBA_MIRROR = "http://samba.org/samba/ftp"
MIRRORS += "\
${SAMBA_MIRROR}    http://mirror.internode.on.net/pub/samba \n \
${SAMBA_MIRROR}    http://www.mirrorservice.org/sites/ftp.samba.org \n \
"

export PYTHONHASHSEED = "1"

SRC_URI = "${SAMBA_MIRROR}/stable/samba-${PV}.tar.gz \
           file://smb.conf \
           file://volatiles.03_samba \
           file://0001-Don-t-check-xsltproc-manpages.patch \
           file://0002-do-not-import-target-module-while-cross-compile.patch \
           file://0003-Add-config-option-without-valgrind.patch \
           file://0004-Add-options-to-configure-the-use-of-libbsd.patch \
           file://0005-Fix-pyext_PATTERN-for-cross-compilation.patch \
           file://0006-smbtorture-skip-test-case-tfork_cmd_send.patch \
           file://0007-Deleted-settiong-of-python-to-fix-the-install-confli.patch \
           "

SRC_URI:append:libc-musl = " \
           file://samba-pam.patch \
           file://samba-4.3.9-remove-getpwent_r.patch \
           "

SRC_URI[sha256sum] = "1aeff76c207f383477ce4badebd154691c408d2e15b01b333c85eb775468ddf6"

UPSTREAM_CHECK_REGEX = "samba\-(?P<pver>4\.19(\.\d+)+).tar.gz"

inherit systemd waf-samba cpan-base perlnative update-rc.d perl-version pkgconfig

CVE_STATUS[CVE-2011-2411] = "not-applicable-platform: vulnerable only on HP NonStop Servers"

# remove default added RDEPENDS on perl
RDEPENDS:${PN}:remove = "perl"

DEPENDS += "readline virtual/libiconv zlib popt libtalloc libtdb libtevent libldb libaio libpam libtasn1 libtasn1-native jansson libparse-yapp-perl-native gnutls cmocka"

inherit features_check
REQUIRED_DISTRO_FEATURES = "pam"

DEPENDS:append:libc-musl = " libtirpc"
CFLAGS:append:libc-musl = " -I${STAGING_INCDIR}/tirpc"
LDFLAGS:append:libc-musl = " -ltirpc"

COMPATIBLE_HOST:riscv32 = "null"

INITSCRIPT_NAME = "samba"
INITSCRIPT_PARAMS = "start 20 3 5 . stop 20 0 1 6 ."

SYSTEMD_PACKAGES = "${PN}-base ${PN}-ad-dc winbind ctdb"
SYSTEMD_SERVICE:${PN}-base = "nmb.service smb.service"
SYSTEMD_SERVICE:${PN}-ad-dc = "${@bb.utils.contains('PACKAGECONFIG', 'ad-dc', 'samba.service', '', d)}"
SYSTEMD_SERVICE:winbind = "winbind.service"
SYSTEMD_SERVICE:ctdb = "ctdb.service"

# There are prerequisite settings to enable ad-dc, so disable the service by default.
# Reference:
# https://wiki.samba.org/index.php/Setting_up_Samba_as_an_Active_Directory_Domain_Controller
SYSTEMD_AUTO_ENABLE:${PN}-ad-dc = "disable"

#cross_compile cannot use preforked process, since fork process earlier than point subproces.popen
#to cross Popen
export WAF_NO_PREFORK = "yes"

# Use krb5. Build active domain controller.
#
PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd zeroconf', d)} \
                   acl cups ldap mitkrb5 \
"

PACKAGECONFIG[acl] = "--with-acl-support,--without-acl-support,acl"
PACKAGECONFIG[fam] = "--with-fam,--without-fam,gamin"
PACKAGECONFIG[cups] = "--enable-cups,--disable-cups,cups"
PACKAGECONFIG[ldap] = "--with-ldap,--without-ldap,openldap"
PACKAGECONFIG[sasl] = ",,cyrus-sasl"
PACKAGECONFIG[systemd] = "--with-systemd,--without-systemd,systemd"
PACKAGECONFIG[dmapi] = "--with-dmapi,--without-dmapi,dmapi"
PACKAGECONFIG[zeroconf] = "--enable-avahi,--disable-avahi,avahi"
PACKAGECONFIG[valgrind] = "--with-valgrind,--without-valgrind,valgrind"
PACKAGECONFIG[lttng] = "--with-lttng,--without-lttng,lttng-ust"
PACKAGECONFIG[archive] = "--with-libarchive,--without-libarchive,libarchive"
PACKAGECONFIG[libunwind] = "--with-libunwind,--without-libunwind,libunwind"
PACKAGECONFIG[gpgme] = "--with-gpgme,--without-gpgme,gpgme"
PACKAGECONFIG[lmdb] = ",--without-ldb-lmdb,lmdb"
PACKAGECONFIG[libbsd] = "--with-libbsd,--without-libbsd,libbsd"
PACKAGECONFIG[ad-dc] = "--with-experimental-mit-ad-dc,--without-ad-dc,python3-markdown python3-dnspython,"
PACKAGECONFIG[mitkrb5] = "--with-system-mitkrb5 --with-system-mitkdc=/usr/sbin/krb5kdc,,krb5,"

SAMBA4_IDMAP_MODULES = "idmap_ad,idmap_rid,idmap_adex,idmap_hash,idmap_tdb2"
SAMBA4_PDB_MODULES = "pdb_tdbsam,${@bb.utils.contains('PACKAGECONFIG', 'ldap', 'pdb_ldap,', '', d)}pdb_ads,pdb_smbpasswd,pdb_wbc_sam,pdb_samba4"
SAMBA4_AUTH_MODULES = "auth_unix,auth_wbc,auth_server,auth_netlogond,auth_script,auth_samba4"
SAMBA4_MODULES = "${SAMBA4_IDMAP_MODULES},${SAMBA4_PDB_MODULES},${SAMBA4_AUTH_MODULES}"

# These libraries are supposed to replace others supplied by packages, but decorate the names of
# .so files so there will not be a conflict.  This is not done consistantly, so be very careful
# when adding to this list.
#
SAMBA4_LIBS = "heimdal,NONE"

EXTRA_OECONF += "--enable-fhs \
                 --with-piddir=/run \
                 --with-sockets-dir=/run/samba \
                 --with-modulesdir=${libdir}/samba \
                 --with-privatelibdir=${libdir}/samba \
                 --with-lockdir=${localstatedir}/lib/samba \
                 --with-cachedir=${localstatedir}/lib/samba \
                 --disable-rpath-install \
                 --disable-rpath \
                 --with-shared-modules=${SAMBA4_MODULES} \
                 --bundled-libraries=${SAMBA4_LIBS} \
                 ${@oe.utils.conditional('TARGET_ARCH', 'x86_64', '', '--disable-glusterfs', d)} \
                 --with-cluster-support \
                 --with-profiling-data \
                 --with-libiconv=${STAGING_DIR_HOST}${prefix} \
                 --with-pam --with-pammodulesdir=${base_libdir}/security \
                 --pythondir=${PYTHON_SITEPACKAGES_DIR} \
                "

LDFLAGS += "-Wl,-z,relro,-z,now ${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', ' -fuse-ld=bfd ', '', d)}"

do_configure:append() {
    cd ${S}/pidl/
    perl Makefile.PL PREFIX=${prefix}
    sed -e 's,VENDORPREFIX)/lib/perl,VENDORPREFIX)/${baselib}/perl,g' \
        -e 's,PERLPREFIX)/lib/perl,PERLPREFIX)/${baselib}/perl,g' -i Makefile
}

do_compile:append() {
    oe_runmake -C ${S}/pidl
}

do_install:append() {
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
    install -m644 ${UNPACKDIR}/smb.conf ${D}${sysconfdir}/samba/smb.conf
    install -D -m 644 ${UNPACKDIR}/volatiles.03_samba ${D}${sysconfdir}/default/volatiles/03_samba

    install -d ${D}${sysconfdir}/default
    install -m644 packaging/systemd/samba.sysconfig ${D}${sysconfdir}/default/samba

    # the items are from ctdb/tests/run_tests.sh
    for d in cunit eventd eventscripts onnode shellcheck takeover takeover_helper tool; do
        testdir=${D}${datadir}/ctdb-tests/UNIT/$d
        install -d $testdir
        cp ${S}/ctdb/tests/UNIT/$d/*.sh $testdir
        cp -r ${S}/ctdb/tests/UNIT/$d/scripts ${S}/ctdb/tests/UNIT/$d/stubs $testdir || true
    done

    # fix file-rdeps qa warning
    if [ -f ${D}${bindir}/onnode ]; then
        sed -i 's:\(#!/bin/\)bash:\1sh:' ${D}${bindir}/onnode
    fi

    chmod 0750 ${D}${sysconfdir}/sudoers.d || true
    rm -rf ${D}/run ${D}${localstatedir}/run ${D}${localstatedir}/log

    for f in samba-gpupdate samba_upgradedns samba_spnupdate samba_kcc samba_dnsupdate samba_downgrade_db; do
        if [ -f "${D}${sbindir}/$f" ]; then
            sed -i -e 's,${PYTHON},/usr/bin/env python3,g' ${D}${sbindir}/$f
        fi
    done
    if [ -f "${D}${bindir}/samba-tool" ]; then
        sed -i -e 's,${PYTHON},/usr/bin/env python3,g' ${D}${bindir}/samba-tool
    fi

    oe_runmake -C ${S}/pidl DESTDIR=${D} install_vendor
    find ${D}${libdir}/perl5/ -type f -name "perllocal.pod" -delete
    find ${D}${libdir}/perl5/ -type f -name ".packlist" -delete
    sed -i -e '1s,#!.*perl,#!${bindir}/env perl,' ${D}${bindir}/pidl
}

PACKAGES =+ "${PN}-python3 ${PN}-pidl \
             ${PN}-dsdb-modules ${PN}-testsuite registry-tools \
             winbind ctdb ctdb-tests \
             ${PN}-common ${PN}-base ${PN}-ad-dc \
             smbclient ${PN}-client ${PN}-server ${PN}-test"

python samba_populate_packages() {
    def module_hook(file, pkg, pattern, format, basename):
        pn = d.getVar('PN')
        d.appendVar('RRECOMMENDS:%s-base' % pn, ' %s' % pkg)

    mlprefix = d.getVar('MLPREFIX') or ''
    pam_libdir = d.expand('${base_libdir}/security')
    pam_pkgname = mlprefix + 'pam-plugin%s'
    do_split_packages(d, pam_libdir, r'^pam_(.*)\.so$', pam_pkgname, 'PAM plugin for %s', extra_depends='', prepend=True)

    libdir = d.getVar('libdir')
    do_split_packages(d, libdir, r'^lib(.*)\.so\..*$', 'lib%s', 'Samba %s library', extra_depends='${PN}-common', prepend=True, allow_links=True)
    pkglibdir = '%s/samba' % libdir
    do_split_packages(d, pkglibdir, r'^lib(.*)\.so$', 'lib%s', 'Samba %s library', extra_depends='${PN}-common', prepend=True)
    moduledir = '%s/samba/auth' % libdir
    do_split_packages(d, moduledir, r'^(.*)\.so$', 'samba-auth-%s', 'Samba %s authentication backend', hook=module_hook, extra_depends='', prepend=True)
    moduledir = '%s/samba/pdb' % libdir
    do_split_packages(d, moduledir, r'^(.*)\.so$', 'samba-pdb-%s', 'Samba %s password backend', hook=module_hook, extra_depends='', prepend=True)
}

PACKAGESPLITFUNCS:prepend = "samba_populate_packages "
PACKAGES_DYNAMIC = "samba-auth-.* samba-pdb-.*"

RDEPENDS:${PN} += "${PN}-base ${PN}-python3 ${PN}-dsdb-modules python3"
RDEPENDS:${PN}-python3 += "pytalloc python3-tdb pyldb"

FILES:${PN}-base = "${sbindir}/nmbd \
                    ${sbindir}/smbd \
                    ${sysconfdir}/init.d \
                    ${systemd_system_unitdir}/nmb.service \
                    ${systemd_system_unitdir}/smb.service"

FILES:${PN}-ad-dc = "${sbindir}/samba \
                     ${systemd_system_unitdir}/samba.service \
                     ${libdir}/krb5/plugins/kdb/samba.so \
"

RDEPENDS:${PN}-ad-dc = "krb5-kdc"

FILES:ctdb = "${bindir}/ctdb \
              ${bindir}/ctdb_diagnostics \
              ${bindir}/ltdbtool \
              ${bindir}/onnode \
              ${bindir}/ping_pong \
              ${sbindir}/ctdbd \
              ${datadir}/ctdb \
              ${libexecdir}/ctdb \
              ${localstatedir}/lib/ctdb \
              ${sysconfdir}/ctdb \
              ${sysconfdir}/sudoers.d/ctdb \
              ${systemd_system_unitdir}/ctdb.service \
"

FILES:ctdb-tests = "${bindir}/ctdb_run_tests \
                    ${bindir}/ctdb_run_cluster_tests \
                    ${datadir}/ctdb-tests \
                    ${datadir}/ctdb/tests \
"

RDEPENDS:ctdb-tests += "bash util-linux-getopt ctdb"

FILES:${BPN}-common = "${sysconfdir}/default \
                       ${sysconfdir}/samba \
                       ${sysconfdir}/tmpfiles.d \
                       ${localstatedir}/lib/samba \
                       ${localstatedir}/spool/samba \
"

FILES:${PN} += "${libdir}/vfs/*.so \
                ${libdir}/charset/*.so \
                ${libdir}/*.dat \
                ${libdir}/auth/*.so \
"

FILES:${PN}-dsdb-modules = "${libdir}/samba/ldb"

FILES:${PN}-testsuite = "${bindir}/gentest \
                         ${bindir}/locktest \
                         ${bindir}/masktest \
                         ${bindir}/ndrdump \
                         ${bindir}/smbtorture"

FILES:registry-tools = "${bindir}/regdiff \
                        ${bindir}/regpatch \
                        ${bindir}/regshell \
                        ${bindir}/regtree"

FILES:winbind = "${sbindir}/winbindd \
                 ${bindir}/wbinfo \
                 ${bindir}/ntlm_auth \
                 ${libdir}/samba/idmap \
                 ${libdir}/samba/nss_info \
                 ${libdir}/winbind_krb5_locator.so \
                 ${libdir}/winbind-krb5-localauth.so \
                 ${sysconfdir}/init.d/winbind \
                 ${systemd_system_unitdir}/winbind.service"

FILES:${PN}-python3 = "${PYTHON_SITEPACKAGES_DIR}"

FILES:smbclient = "${bindir}/cifsdd \
                   ${bindir}/rpcclient \
                   ${bindir}/smbcacls \
                   ${bindir}/smbclient \
                   ${bindir}/smbcquotas \
                   ${bindir}/smbget \
                   ${bindir}/smbspool \
                   ${bindir}/smbtar \
                   ${bindir}/smbtree \
                   ${libexecdir}/samba/smbspool_krb5_wrapper"

FILES:${PN}-pidl = "${bindir}/pidl \
                    ${libdir}/perl5 \
                   "
RDEPENDS:${PN}-pidl += "perl perl-modules libparse-yapp-perl"

RDEPENDS:${PN}-client = "\
    smbclient \
    winbind \
    registry-tools \
    ${PN}-pidl \
    "

ALLOW_EMPTY:${PN}-client = "1"

RDEPENDS:${PN}-server = "\
    ${PN} \
    winbind \
    registry-tools \
    "

ALLOW_EMPTY:${PN}-server = "1"

RDEPENDS:${PN}-test = "\
    ctdb-tests \
    ${PN}-testsuite \
    "

ALLOW_EMPTY:${PN}-test = "1"
