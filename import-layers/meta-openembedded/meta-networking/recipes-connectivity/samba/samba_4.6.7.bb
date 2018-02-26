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
           file://16-do-not-check-xsltproc-manpages.patch \
           file://20-do-not-import-target-module-while-cross-compile.patch \
           file://21-add-config-option-without-valgrind.patch \
           file://0001-packaging-Avoid-timeout-for-nmbd-if-started-offline-.patch \
           file://0006-avoid-using-colon-in-the-checking-msg.patch \
           file://netdb_defines.patch \
           file://glibc_only.patch \
           file://volatiles.03_samba \
          "
SRC_URI_append_libc-musl = " \
           file://samba-4.2.7-pam.patch \
           file://samba-4.3.9-remove-getpwent_r.patch \
          "

SRC_URI[md5sum] = "c6ee5c766016d59908c8fb672fbbd445"
SRC_URI[sha256sum] = "9ef24393de08390f236cabccd6a420b5cea304e959cbf1a99ff317325db3ddfa"

inherit systemd waf-samba cpan-base perlnative update-rc.d
# remove default added RDEPENDS on perl
RDEPENDS_${PN}_remove = "perl"

DEPENDS += "readline virtual/libiconv zlib popt libtalloc libtdb libtevent libldb krb5 libbsd libaio libpam"
DEPENDS_append_libc-musl = " libtirpc"
CFLAGS_append_libc-musl = " -I${STAGING_INCDIR}/tirpc"
LDFLAGS_append_libc-musl = " -ltirpc"

LSB = ""
LSB_linuxstdbase = "lsb"

INITSCRIPT_NAME = "samba"
INITSCRIPT_PARAMS = "start 20 3 5 . stop 20 0 1 6 ."

SYSTEMD_PACKAGES = "${PN}-base winbind"
SYSTEMD_SERVICE_${PN}-base = "nmb.service smb.service"
SYSTEMD_SERVICE_winbind = "winbind.service"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd zeroconf', d)} \
                   acl cups ldap \
"

RDEPENDS_${PN}-base += "${LSB}"
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


SAMBA4_IDMAP_MODULES="idmap_ad,idmap_rid,idmap_adex,idmap_hash,idmap_tdb2"
SAMBA4_PDB_MODULES="pdb_tdbsam,${@bb.utils.contains('PACKAGECONFIG', 'ldap', 'pdb_ldap,', '', d)}pdb_ads,pdb_smbpasswd,pdb_wbc_sam,pdb_samba4"
SAMBA4_AUTH_MODULES="auth_unix,auth_wbc,auth_server,auth_netlogond,auth_script,auth_samba4"
SAMBA4_MODULES="${SAMBA4_IDMAP_MODULES},${SAMBA4_PDB_MODULES},${SAMBA4_AUTH_MODULES}"

SAMBA4_LIBS="heimdal,!zlib,!popt,!talloc,!pytalloc,!pytalloc-util,!tevent,!pytevent,!tdb,!pytdb,!ldb,!pyldb"

EXTRA_OECONF += "--enable-fhs \
                 --with-piddir=/run \
                 --with-sockets-dir=/run/samba \
                 --with-modulesdir=${libdir}/samba \
                 --with-lockdir=${localstatedir}/lib/samba \
                 --with-cachedir=${localstatedir}/lib/samba \
                 --disable-gnutls \
                 --disable-rpath-install \
                 --with-shared-modules=${SAMBA4_MODULES} \
                 --bundled-libraries=${SAMBA4_LIBS} \
                 --with-system-mitkrb5 \
                 --without-ad-dc \
                 ${@base_conditional('TARGET_ARCH', 'x86_64', '', '--disable-glusterfs', d)} \
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
    install -m 0644 packaging/systemd/*.service ${D}${systemd_system_unitdir}
    sed -e 's,\(ExecReload=\).*\(/kill\),\1${base_bindir}\2,' \
        -e 's,/etc/sysconfig/samba,${sysconfdir}/default/samba,' \
        -i ${D}${systemd_system_unitdir}/*.service

    install -d ${D}${sysconfdir}/tmpfiles.d
    install -m644 packaging/systemd/samba.conf.tmp ${D}${sysconfdir}/tmpfiles.d/samba.conf
    echo "d ${localstatedir}/log/samba 0755 root root -" \
        >> ${D}${sysconfdir}/tmpfiles.d/samba.conf
    if [ "${LSB}" = "lsb" ]; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 packaging/LSB/samba.sh ${D}${sysconfdir}/init.d/samba
    else
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 packaging/sysv/samba.init ${D}${sysconfdir}/init.d/samba
        sed -e 's,/opt/samba/bin,${sbindir},g' \
            -e 's,/opt/samba/smb.conf,${sysconfdir}/samba/smb.conf,g' \
            -e 's,/opt/samba/log,${localstatedir}/log/samba,g' \
            -e 's,/etc/init.d/samba.server,${sysconfdir}/init.d/samba,g' \
            -e 's,/usr/bin,${base_bindir},g' \
            -i ${D}${sysconfdir}/init.d/samba
    fi

    install -d ${D}${sysconfdir}/samba
    echo "127.0.0.1 localhost" > ${D}${sysconfdir}/samba/lmhosts
    install -m644 packaging/LSB/smb.conf ${D}${sysconfdir}/samba/smb.conf
    install -D -m 644 ${WORKDIR}/volatiles.03_samba ${D}${sysconfdir}/default/volatiles/03_samba

    install -d ${D}${sysconfdir}/default
    install -m644 packaging/systemd/samba.sysconfig ${D}${sysconfdir}/default/samba

    # install ctdb config file and test cases
    install -D -m 0644 ${S}/ctdb/tests/onnode/nodes ${D}${sysconfdir}/ctdb/nodes
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

    chmod 0750 ${D}${sysconfdir}/sudoers.d
    rm -rf ${D}/run ${D}${localstatedir}/run ${D}${localstatedir}/log
}

PACKAGES =+ "${PN}-python ${PN}-pidl \
             ${PN}-dsdb-modules ${PN}-testsuite registry-tools \
             winbind \
             ${PN}-common ${PN}-base ${PN}-ctdb-tests \
             smbclient"

python samba_populate_packages() {
    def module_hook(file, pkg, pattern, format, basename):
        pn = d.getVar('PN', True)
        d.appendVar('RRECOMMENDS_%s-base' % pn, ' %s' % pkg)

    mlprefix = d.getVar('MLPREFIX', True) or ''
    pam_libdir = d.expand('${base_libdir}/security')
    pam_pkgname = mlprefix + 'pam-plugin%s'
    do_split_packages(d, pam_libdir, '^pam_(.*)\.so$', pam_pkgname, 'PAM plugin for %s', extra_depends='', prepend=True)

    libdir = d.getVar('libdir', True)
    do_split_packages(d, libdir, '^lib(.*)\.so\..*$', 'lib%s', 'Samba %s library', extra_depends='${PN}-common', prepend=True, allow_links=True)
    pkglibdir = '%s/samba' % libdir
    do_split_packages(d, pkglibdir, '^lib(.*)\.so$', 'lib%s', 'Samba %s library', extra_depends='${PN}-common', prepend=True)
    moduledir = '%s/samba/auth' % libdir
    do_split_packages(d, moduledir, '^(.*)\.so$', 'samba-auth-%s', 'Samba %s authentication backend', hook=module_hook, extra_depends='', prepend=True)
    moduledir = '%s/samba/pdb' % libdir
    do_split_packages(d, moduledir, '^(.*)\.so$', 'samba-pdb-%s', 'Samba %s password backend', hook=module_hook, extra_depends='', prepend=True)
}

PACKAGESPLITFUNCS_prepend = "samba_populate_packages "

RDEPENDS_${PN} += "${PN}-base ${PN}-python ${PN}-dsdb-modules"
RDEPENDS_${PN}-python += "pytalloc python-tdb"

FILES_${PN}-base = "${sbindir}/nmbd \
                    ${sbindir}/smbd \
                    ${sysconfdir}/init.d \
                    ${localstatedir}/lib/samba \
                    ${localstatedir}/nmbd \
                    ${localstatedir}/spool/samba \
                    ${systemd_system_unitdir}/nmb.service \
                    ${systemd_system_unitdir}/samba.service \
                    ${systemd_system_unitdir}/smb.service"

FILES_${PN}-ctdb-tests = "${bindir}/ctdb_run_tests \
                          ${bindir}/ctdb_run_cluster_tests \
                          ${sysconfdir}/ctdb/nodes \
                          ${datadir}/ctdb-tests \
                          ${datadir}/ctdb/tests \
                         "

FILES_${BPN}-common = "${sysconfdir}/default \
                       ${sysconfdir}/samba \
                       ${sysconfdir}/tmpfiles.d \
"

FILES_${PN} += "${libdir}/vfs/*.so \
                ${libdir}/charset/*.so \
                ${libdir}/*.dat \
                ${libdir}/auth/*.so \
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
                 ${sysconfdir}/init.d/winbind \
                 ${systemd_system_unitdir}/winbind.service"

FILES_${PN}-python = "${PYTHON_SITEPACKAGES_DIR}"

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
