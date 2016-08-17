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
           file://00-fix-typos-in-man-pages.patch \
           file://16-do-not-check-xsltproc-manpages.patch \
           file://20-do-not-import-target-module-while-cross-compile.patch \
           file://21-add-config-option-without-valgrind.patch \
           file://0006-avoid-using-colon-in-the-checking-msg.patch \
           file://volatiles.03_samba \
          "

SRC_URI[md5sum] = "03a65a3adf08ceb1636ad59d234d7f9d"
SRC_URI[sha256sum] = "eaecd41a85ebb9507b8db9856ada2a949376e9d53cf75664b5493658f6e5926a"

inherit systemd waf-samba cpan-base perlnative
# remove default added RDEPENDS on perl
RDEPENDS_${PN}_remove = "perl"

DEPENDS += "readline virtual/libiconv zlib popt libtalloc libtdb libtevent libldb krb5 libbsd libaio"

SYSVINITTYPE_linuxstdbase = "lsb"
SYSVINITTYPE = "sysv"

PACKAGECONFIG ??= "${@base_contains('DISTRO_FEATURES', 'pam', 'pam', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', '${SYSVINITTYPE}', '', d)} \
                   ${@base_contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)} \
                   ${@base_contains('DISTRO_FEATURES', 'zeroconf', 'zeroconf', '', d)} \
                   acl cups ldap \
"

RDEPENDS_${PN}-base += "${@bb.utils.contains('PACKAGECONFIG', 'lsb', 'lsb', '', d)}"
RDEPENDS_${PN}-ctdb-tests += "bash"

PACKAGECONFIG[acl] = "--with-acl-support,--without-acl-support,acl"
PACKAGECONFIG[fam] = "--with-fam,--without-fam,gamin"
PACKAGECONFIG[pam] = "--with-pam --with-pammodulesdir=${base_libdir}/security,--without-pam,libpam"
PACKAGECONFIG[lsb] = ",,lsb"
PACKAGECONFIG[sysv] = ",,sysvinit"
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
                "
DISABLE_STATIC = ""

LDFLAGS += "-Wl,-z,relro,-z,now"

do_install_append() {
    if ${@bb.utils.contains('PACKAGECONFIG', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_unitdir}/system
        for i in nmb smb winbind; do
            install -m 0644 packaging/systemd/$i.service ${D}${systemd_unitdir}/system
        done
        sed -i 's,\(ExecReload=\).*\(/kill\),\1${base_bindir}\2,' ${D}${systemd_unitdir}/system/*.service

        install -d ${D}${sysconfdir}/tmpfiles.d
        install -m644 packaging/systemd/samba.conf.tmp ${D}${sysconfdir}/tmpfiles.d/samba.conf
        echo "d ${localstatedir}/log/samba 0755 root root -" \
            >> ${D}${sysconfdir}/tmpfiles.d/samba.conf
    elif ${@bb.utils.contains('PACKAGECONFIG', 'lsb', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 packaging/LSB/samba.sh ${D}${sysconfdir}/init.d
        update-rc.d -r ${D} samba.sh start 20 3 5 .
        update-rc.d -r ${D} samba.sh start 20 0 1 6 .
    elif ${@bb.utils.contains('PACKAGECONFIG', 'sysv', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 packaging/sysv/samba.init ${D}${sysconfdir}/init.d/samba.sh
        sed -e 's,/opt/samba/bin,${sbindir},g' \
            -e 's,/opt/samba/smb.conf,${sysconfdir}/samba/smb.conf,g' \
            -e 's,/opt/samba/log,${localstatedir}/log/samba,g' \
            -e 's,/etc/init.d/samba.server,${sysconfdir}/init.d/samba.sh,g' \
            -i ${D}${sysconfdir}/init.d/samba.sh
        update-rc.d -r ${D} samba.sh start 20 3 5 .
        update-rc.d -r ${D} samba.sh start 20 0 1 6 .
    fi

    install -d ${D}${sysconfdir}/samba
    echo "127.0.0.1 localhost" > ${D}${sysconfdir}/samba/lmhosts
    install -m644 packaging/LSB/smb.conf ${D}${sysconfdir}/samba/smb.conf
    install -D -m 644 ${WORKDIR}/volatiles.03_samba ${D}${sysconfdir}/default/volatiles/03_samba

    install -d ${D}${sysconfdir}/sysconfig/
    install -m644 packaging/systemd/samba.sysconfig ${D}${sysconfdir}/sysconfig/samba

    rm -rf ${D}/run ${D}${localstatedir}/run
}

PACKAGES += "${PN}-python ${PN}-python-dbg ${PN}-pidl libwinbind libwinbind-dbg libwinbind-krb5-locator"
PACKAGES =+ "libwbclient libnss-winbind winbind winbind-dbg libnetapi libsmbsharemodes \
             libsmbclient libsmbclient-dev lib${PN}-base ${PN}-base ${PN}-ctdb-tests"

RDEPENDS_${PN} += "${PN}-base"

FILES_${PN}-base = "${sbindir}/nmbd \
                    ${sbindir}/smbd \
                    ${sysconfdir}/init.d \
                    ${localstatedir}/lib/samba \
                    ${localstatedir}/log/samba \
                    ${localstatedir}/nmbd \
                    ${localstatedir}/spool/samba \
"

FILES_${PN}-ctdb-tests = "${bindir}/ctdb_run_tests \
                          ${libdir}/ctdb-tests \
                          ${datadir}/ctdb-tests \
                          /run/ctdb \
                         "

# figured out by
# FILES="tmp/work/cortexa9hf-vfp-neon-poky-linux-gnueabi/samba/4.1.12-r0/image/usr/sbin/smbd tmp/work/cortexa9hf-vfp-neon-poky-linux-gnueabi/samba/4.1.12-r0/image/usr/sbin/nmbd"
#
# while [ "${FILES}" != "${OLDFILES}" ]
# do
#     OLDFILES="${FILES}"
#     NEEDED=`tmp/sysroots/x86_64-linux/usr/libexec/arm-poky-linux-gnueabi.gcc-cross-initial-arm/gcc/arm-poky-linux-gnueabi/5.2.0/objdump -x ${FILES} | grep NEEDED | egrep -E 'so(.[0-9]|$)' | sort -u | perl -MData::Dumper -le 'while (<>) {chomp; push @lib, (split)[1]}; print "(", join("|", @lib), ")"'`
#     NF=`find tmp/work/cortexa9hf-vfp-neon-poky-linux-gnueabi/samba/4.1.12-r0/image/usr/lib -type f | egrep "${NEEDED}" | sort -u`
#
#     FILES=`perl -le 'foreach (@ARGV) { $f{$_}++ }; print join(" ", sort keys %f)' ${FILES} ${NF}`
# done
#
# LIBS=`echo ${FILES} | sed -e 's,tmp/work/cortexa9hf-vfp-neon-poky-linux-gnueabi/samba/4.1.12-r0/image/usr/lib,${libdir},g' -e 's,.so.[0-9]+.*$,.so.*,g'`
# for l in ${LIBS}
# do
#     echo $l
# done

FILES_lib${PN}-base = "\
                    ${sysconfdir}/default \
                    ${sysconfdir}/samba \
                    ${libdir}/libdcerpc-binding.so.* \
                    ${libdir}/libgensec.so.* \
                    ${libdir}/libndr-krb5pac.so.* \
                    ${libdir}/libndr-nbt.so.* \
                    ${libdir}/libndr-standard.so.* \
                    ${libdir}/libndr.so.* \
                    ${libdir}/libnetapi.so.* \
                    ${libdir}/libpdb.so.* \
                    ${libdir}/libsamba-credentials.so.* \
                    ${libdir}/libsamba-hostconfig.so.* \
                    ${libdir}/libsamba-util.so.* \
                    ${libdir}/libsamdb.so.* \
                    ${libdir}/libsmbconf.so.* \
                    ${libdir}/libtevent-util.so.* \
                    ${libdir}/samba/libCHARSET3.so \
                    ${libdir}/samba/libaddns.so \
                    ${libdir}/samba/libads.so \
                    ${libdir}/samba/libasn1util.so \
                    ${libdir}/samba/libauth.so \
                    ${libdir}/samba/libauth_sam_reply.so \
                    ${libdir}/samba/libauthkrb5.so \
                    ${libdir}/samba/libccan.so \
                    ${libdir}/samba/libcli-ldap-common.so \
                    ${libdir}/samba/libcli-nbt.so \
                    ${libdir}/samba/libcli_cldap.so \
                    ${libdir}/samba/libcli_smb_common.so \
                    ${libdir}/samba/libcli_spoolss.so \
                    ${libdir}/samba/libcliauth.so \
                    ${libdir}/samba/libdbwrap.so \
                    ${libdir}/samba/libdcerpc-samba.so \
                    ${libdir}/samba/liberrors.so \
                    ${libdir}/samba/libflag_mapping.so \
                    ${libdir}/samba/libgse.so \
                    ${libdir}/samba/libinterfaces.so \
                    ${libdir}/samba/libkrb5samba.so \
                    ${libdir}/samba/libldbsamba.so \
                    ${libdir}/samba/liblibcli_lsa3.so \
                    ${libdir}/samba/liblibcli_netlogon3.so \
                    ${libdir}/samba/liblibsmb.so \
                    ${libdir}/samba/libmsrpc3.so \
                    ${libdir}/samba/libndr-samba.so \
                    ${libdir}/samba/libndr-samba4.so \
                    ${libdir}/samba/libnpa_tstream.so \
                    ${libdir}/samba/libntdb.so.* \
                    ${libdir}/samba/libpopt_samba3.so \
                    ${libdir}/samba/libprinting_migrate.so \
                    ${libdir}/samba/libsamba-modules.so \
                    ${libdir}/samba/libsamba-security.so \
                    ${libdir}/samba/libsamba-sockets.so \
                    ${libdir}/samba/libsamba3-util.so \
                    ${libdir}/samba/libsamdb-common.so \
                    ${libdir}/samba/libsecrets3.so \
                    ${libdir}/samba/libserver-role.so \
                    ${libdir}/samba/libsmb_transport.so \
                    ${libdir}/samba/libsmbd_base.so \
                    ${libdir}/samba/libsmbd_conn.so \
                    ${libdir}/samba/libsmbd_shim.so \
                    ${libdir}/samba/libsmbregistry.so \
                    ${libdir}/samba/libtdb-wrap.so \
                    ${libdir}/samba/libutil_cmdline.so \
                    ${libdir}/samba/libutil_ntdb.so \
                    ${libdir}/samba/libutil_reg.so \
                    ${libdir}/samba/libutil_setid.so \
                    ${libdir}/samba/libutil_tdb.so \
                    ${libdir}/samba/pdb/smbpasswd.so \
                    ${libdir}/samba/pdb/tdbsam.so \
                    ${libdir}/samba/pdb/wbc_sam.so \
"

FILES_winbind-dbg = "${libdir}/idmap/.debug/*.so \
                     ${libdir}/security/.debug/pam_winbind.so \
"

FILES_${PN} += "${libdir}/vfs/*.so \
                ${libdir}/charset/*.so \
                ${libdir}/*.dat \
                ${libdir}/auth/*.so \
                ${libdir}/security/pam_smbpass.so \
"

FILES_${PN}-dbg += "${libdir}/vfs/.debug/*.so \
                    ${libdir}/charset/.debug/*.so \
                    ${libdir}/auth/.debug/*.so \
                    ${libdir}/security/.debug/pam_smbpass.so \
"

FILES_libwbclient = "${libdir}/libwbclient.so.* ${libdir}/samba/libwinbind-client.so"
FILES_libnetapi = "${libdir}/libnetapi.so.*"
FILES_libsmbsharemodes = "${libdir}/libsmbsharemodes.so.*"
FILES_libsmbclient = "${libdir}/libsmbclient.so.*"
FILES_libsmbclient-dev = "${libdir}/libsmbclient.so ${includedir}"
FILES_winbind = "${sbindir}/winbindd \
                 ${bindir}/wbinfo \
                 ${bindir}/ntlm_auth \
                 ${sysconfdir}/init.d/winbind \
                 ${systemd_unitdir}/system/winbind.service \
"

FILES_libnss-winbind = "${libdir}/libnss_*${SOLIBS} \
                        ${libdir}/nss_info \
"

FILES_${PN} += "${base_libdir}/security/pam_smbpass.so \
"

SMB_SERVICE="${systemd_unitdir}/system/nmb.service ${systemd_unitdir}/system/smb.service"
SMB_SYSV="${sysconfdir}/init.d ${sysconfdir}/rc?.d"
FILES_${PN}-base +="${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${SMB_SERVICE}', '', d)}"
FILES_${PN}-base +="${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', '${SMB_SYSV}', '', d)}"

FILES_${PN}-dbg += "${libdir}/samba/idmap/.debug/* \
                    ${libdir}/samba/pdb/.debug/* \
                    ${libdir}/samba/auth/.debug/* \
                    ${libdir}/samba/nss_info/.debug/* \
                    ${libdir}/samba/ldb/.debug/* \
                    ${libdir}/samba/vfs/.debug/* \
                    ${base_libdir}/security/.debug/pam_smbpass.so \
"

FILES_libwinbind = "${base_libdir}/security/pam_winbind.so"
FILES_libwinbind += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${systemd_unitdir}/system/winbind.service', '', d)}"
FILES_libwinbind-dbg = "${base_libdir}/security/.debug/pam_winbind.so"
FILES_libwinbind-krb5-locator = "${libdir}/winbind_krb5_locator.so"

FILES_${PN}-python = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/*.so \
                      ${libdir}/python${PYTHON_BASEVERSION}/site-packages/_ldb_text.py \
                      ${libdir}/python${PYTHON_BASEVERSION}/site-packages/samba/*.py \
                      ${libdir}/python${PYTHON_BASEVERSION}/site-packages/samba/*.so \
                      ${libdir}/python${PYTHON_BASEVERSION}/site-packages/samba/dcerpc/*.so \
                      ${libdir}/python${PYTHON_BASEVERSION}/site-packages/samba/dcerpc/*.py \
                      ${libdir}/python${PYTHON_BASEVERSION}/site-packages/samba/external/* \
                      ${libdir}/python${PYTHON_BASEVERSION}/site-packages/samba/kcc/* \
                      ${libdir}/python${PYTHON_BASEVERSION}/site-packages/samba/netcmd/*.py \
                      ${libdir}/python${PYTHON_BASEVERSION}/site-packages/samba/provision/*.py \
                      ${libdir}/python${PYTHON_BASEVERSION}/site-packages/samba/samba3/*.py \
                      ${libdir}/python${PYTHON_BASEVERSION}/site-packages/samba/samba3/*.so \
                      ${libdir}/python${PYTHON_BASEVERSION}/site-packages/samba/subunit/* \
                      ${libdir}/python${PYTHON_BASEVERSION}/site-packages/samba/tests/* \
                      ${libdir}/python${PYTHON_BASEVERSION}/site-packages/samba/third_party/* \
                      ${libdir}/python${PYTHON_BASEVERSION}/site-packages/samba/web_server/* \
"

FILES_${PN}-python-dbg = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/.debug/* \
                          ${libdir}/python${PYTHON_BASEVERSION}/site-packages/samba/.debug/* \
                          ${libdir}/python${PYTHON_BASEVERSION}/site-packages/samba/samba3/.debug/* \
                          ${libdir}/python${PYTHON_BASEVERSION}/site-packages/samba/dcerpc/.debug/* \
"

RDEPENDS_${PN}-pidl_append = " perl"
FILES_${PN}-pidl = "${bindir}/pidl ${datadir}/perl5/Parse"
