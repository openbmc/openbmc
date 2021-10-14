HOMEPAGE = "https://www.samba.org/"
SECTION = "console/network"

LICENSE = "GPL-3.0+ & LGPL-3.0+ & GPL-2.0+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://${COREBASE}/meta/files/common-licenses/LGPL-3.0-or-later;md5=c51d3eef3be114124d11349ca0d7e117 \
                    file://${COREBASE}/meta/files/common-licenses/GPL-2.0-or-later;md5=fed54355545ffd980b814dab4a3b312c"

SAMBA_MIRROR = "http://samba.org/samba/ftp"
MIRRORS += "\
${SAMBA_MIRROR}    http://mirror.internode.on.net/pub/samba \n \
${SAMBA_MIRROR}    http://www.mirrorservice.org/sites/ftp.samba.org \n \
"

SRC_URI = "${SAMBA_MIRROR}/stable/samba-${PV}.tar.gz \
           file://smb.conf \
           file://volatiles.03_samba \
           file://0001-Don-t-check-xsltproc-manpages.patch \
           file://0002-do-not-import-target-module-while-cross-compile.patch \
           file://0003-Add-config-option-without-valgrind.patch \
           file://0004-Add-options-to-configure-the-use-of-libbsd.patch \
           file://0005-samba-build-dnsserver_common-code.patch \
           file://0006-samba-defeat-iconv-test.patch \
           file://0007-wscript_configure_system_gnutls-disable-check-gnutls.patch \
           file://0008-source3-wscript-disable-check-fcntl-F_OWNER_EX.patch \
           file://0009-source3-wscript-disable-check-fcntl-RW_HINTS.patch \
           "

SRC_URI:append:libc-musl = " \
           file://netdb_defines.patch \
           file://samba-pam.patch \
           file://samba-4.3.9-remove-getpwent_r.patch \
           file://cmocka-uintptr_t.patch \
           file://samba-fix-musl-lib-without-innetgr.patch \
           "

SRC_URI[md5sum] = "f0db8302944bb861b31f4163fd302f66"
SRC_URI[sha256sum] = "6f50353f9602aa20245eb18ceb00e7e5ec793df0974aebd5254c38f16d8f1906"

UPSTREAM_CHECK_REGEX = "samba\-(?P<pver>4\.14(\.\d+)+).tar.gz"

inherit systemd waf-samba cpan-base perlnative update-rc.d perl-version pkgconfig

# CVE-2011-2411 is valnerble only on HP NonStop Servers.
CVE_CHECK_WHITELIST += "CVE-2011-2411" 

# remove default added RDEPENDS on perl
RDEPENDS:${PN}:remove = "perl"

DEPENDS += "readline virtual/libiconv zlib popt libtalloc libtdb libtevent libldb libaio libpam libtasn1 jansson libparse-yapp-perl-native gnutls"

inherit features_check
REQUIRED_DISTRO_FEATURES = "pam"

DEPENDS:append:libc-musl = " libtirpc"
CFLAGS:append:libc-musl = " -I${STAGING_INCDIR}/tirpc"
LDFLAGS:append:libc-musl = " -ltirpc"

COMPATIBLE_HOST:riscv32 = "null"

INITSCRIPT_NAME = "samba"
INITSCRIPT_PARAMS = "start 20 3 5 . stop 20 0 1 6 ."

SYSTEMD_PACKAGES = "${PN}-base ${PN}-ad-dc winbind"
SYSTEMD_SERVICE:${PN}-base = "nmb.service smb.service"
SYSTEMD_SERVICE:${PN}-ad-dc = "${@bb.utils.contains('PACKAGECONFIG', 'ad-dc', 'samba.service', '', d)}"
SYSTEMD_SERVICE:winbind = "winbind.service"

# There are prerequisite settings to enable ad-dc, so disable the service by default.
# Reference:
# https://wiki.samba.org/index.php/Setting_up_Samba_as_an_Active_Directory_Domain_Controller
SYSTEMD_AUTO_ENABLE:${PN}-ad-dc = "disable"

#cross_compile cannot use preforked process, since fork process earlier than point subproces.popen
#to cross Popen
export WAF_NO_PREFORK="yes"

# Use krb5.  Build active domain controller.
#
PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd zeroconf', d)} \
                   acl cups ad-dc ldap mitkrb5 \
"

RDEPENDS:${PN}-ctdb-tests += "bash util-linux-getopt"

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
PACKAGECONFIG[ad-dc] = "--with-experimental-mit-ad-dc,--without-ad-dc,python3-markdown python3-dnspython,"
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

do_configure:append () {
    cd ${S}/pidl/
    perl Makefile.PL PREFIX=${prefix}
    sed -e 's,VENDORPREFIX)/lib/perl,VENDORPREFIX)/${baselib}/perl,g' \
        -e 's,PERLPREFIX)/lib/perl,PERLPREFIX)/${baselib}/perl,g' -i Makefile

}

do_compile:append () {
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
    install -m644 ${WORKDIR}/smb.conf ${D}${sysconfdir}/samba/smb.conf
    install -D -m 644 ${WORKDIR}/volatiles.03_samba ${D}${sysconfdir}/default/volatiles/03_samba

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
    find ${D}${libdir}/ -type f -name "perllocal.pod" | xargs rm -f
    rm -rf ${D}${libdir}/perl5/vendor_perl/${PERLVERSION}/${BUILD_SYS}/auto/Parse/Pidl/.packlist
    sed -i -e '1s,#!.*perl,#!${bindir}/env perl,' ${D}${bindir}/pidl
}

PACKAGES =+ "${PN}-python3 ${PN}-pidl \
             ${PN}-dsdb-modules ${PN}-testsuite registry-tools \
             winbind \
             ${PN}-common ${PN}-base ${PN}-ad-dc ${PN}-ctdb-tests \
             smbclient ${PN}-client ${PN}-server ${PN}-test"

python samba_populate_packages() {
    def module_hook(file, pkg, pattern, format, basename):
        pn = d.getVar('PN')
        d.appendVar('RRECOMMENDS:%s-base' % pn, ' %s' % pkg)

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

PACKAGESPLITFUNCS:prepend = "samba_populate_packages "
PACKAGES_DYNAMIC = "samba-auth-.* samba-pdb-.*"

RDEPENDS:${PN} += "${PN}-base ${PN}-python3 ${PN}-dsdb-modules python3"
RDEPENDS:${PN}-python3 += "pytalloc python3-tdb"

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

FILES:${PN}-ctdb-tests = "${bindir}/ctdb_run_tests \
                          ${bindir}/ctdb_run_cluster_tests \
                          ${sysconfdir}/ctdb/nodes \
                          ${datadir}/ctdb-tests \
                          ${datadir}/ctdb/tests \
                          ${localstatedir}/lib/ctdb \
                         "

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
                ${datadir}/ctdb/events/* \
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
                   ${libdir}/samba/smbspool_krb5_wrapper"

RDEPENDS:${PN}-pidl:append = " perl libparse-yapp-perl"
FILES:${PN}-pidl = "${bindir}/pidl \
                    ${libdir}/perl5 \
                   "

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
    ${PN}-ctdb-tests \
    ${PN}-testsuite \
    "

ALLOW_EMPTY:${PN}-test = "1"
