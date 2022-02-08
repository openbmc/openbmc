SUMMARY = "A suite of security-related network utilities based on \
the SSH protocol including the ssh client and sshd server"
DESCRIPTION = "Secure rlogin/rsh/rcp/telnet replacement (OpenSSH) \
Ssh (Secure Shell) is a program for logging into a remote machine \
and for executing commands on a remote machine."
HOMEPAGE = "http://www.openssh.com/"
SECTION = "console/network"
LICENSE = "BSD-2-Clause & BSD-3-Clause & BSD-4-Clause & ISC & MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=18d9e5a8b3dd1790d73502f50426d4d3"

DEPENDS = "zlib openssl virtual/crypt"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)}"

SRC_URI = "http://ftp.openbsd.org/pub/OpenBSD/OpenSSH/portable/openssh-${PV}.tar.gz \
           file://sshd_config \
           file://ssh_config \
           file://init \
           ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '${PAM_SRC_URI}', '', d)} \
           file://sshd.socket \
           file://sshd@.service \
           file://sshdgenkeys.service \
           file://volatiles.99_sshd \
           file://run-ptest \
           file://fix-potential-signed-overflow-in-pointer-arithmatic.patch \
           file://sshd_check_keys \
           file://add-test-support-for-busybox.patch \
           file://CVE-2020-14145.patch \
           file://CVE-2021-28041.patch \
           file://CVE-2021-41617.patch \
           "
SRC_URI[md5sum] = "3076e6413e8dbe56d33848c1054ac091"
SRC_URI[sha256sum] = "43925151e6cf6cee1450190c0e9af4dc36b41c12737619edff8bcebdff64e671"

# This CVE is specific to OpenSSH with the pam opie which we don't build/use here
CVE_CHECK_WHITELIST += "CVE-2007-2768"

# This CVE is specific to OpenSSH server, as used in Fedora and Red Hat Enterprise Linux 7
# and when running in a Kerberos environment. As such it is not relevant to OpenEmbedded
CVE_CHECK_WHITELIST += "CVE-2014-9278"

# As per upstream, because of the way scp is based on a historical protocol called rcp
# which relies on that style of argument passing and therefore encounters expansion
# problems. Making changes to how the scp command line works breaks the pattern used
# by scp consumers. Upstream therefore recommends the use of rsync in the place of
# scp for better security. https://bugzilla.redhat.com/show_bug.cgi?id=1860487
CVE_CHECK_WHITELIST += "CVE-2020-15778"

# CVE-2008-3844 was reported in OpenSSH on Red Hat Enterprise Linux and
# certain packages may have been compromised. This CVE is not applicable
# as our source is OpenBSD. https://securitytracker.com/id?1020730
# https://www.securityfocus.com/bid/30794
CVE_CHECK_WHITELIST += "CVE-2008-3844"

# openssh-ssh1 is provided for compatibility with old devices that
# cannot be upgraded to modern protocols. Thus they may not provide security
# support for this package because doing so would prevent access to equipment.
# The upstream OpenSSH developers see this as an important
# security feature and do not intend to 'fix' it.
# https://security-tracker.debian.org/tracker/CVE-2016-20012
# https://ubuntu.com/security/CVE-2016-20012
CVE_CHECK_WHITELIST += "CVE-2016-20012"

PAM_SRC_URI = "file://sshd"

inherit manpages useradd update-rc.d update-alternatives systemd

USERADD_PACKAGES = "${PN}-sshd"
USERADD_PARAM_${PN}-sshd = "--system --no-create-home --home-dir /var/run/sshd --shell /bin/false --user-group sshd"
INITSCRIPT_PACKAGES = "${PN}-sshd"
INITSCRIPT_NAME_${PN}-sshd = "sshd"
INITSCRIPT_PARAMS_${PN}-sshd = "defaults 9"

SYSTEMD_PACKAGES = "${PN}-sshd"
SYSTEMD_SERVICE_${PN}-sshd = "sshd.socket"

inherit autotools-brokensep ptest

PACKAGECONFIG ??= "rng-tools"
PACKAGECONFIG[kerberos] = "--with-kerberos5,--without-kerberos5,krb5"
PACKAGECONFIG[ldns] = "--with-ldns,--without-ldns,ldns"
PACKAGECONFIG[libedit] = "--with-libedit,--without-libedit,libedit"
PACKAGECONFIG[manpages] = "--with-mantype=man,--with-mantype=cat"

# Add RRECOMMENDS to rng-tools for sshd package
PACKAGECONFIG[rng-tools] = ""

EXTRA_AUTORECONF += "--exclude=aclocal"

# login path is hardcoded in sshd
EXTRA_OECONF = "'LOGIN_PROGRAM=${base_bindir}/login' \
                ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '--with-pam', '--without-pam', d)} \
                --without-zlib-version-check \
                --with-privsep-path=${localstatedir}/run/sshd \
                --sysconfdir=${sysconfdir}/ssh \
                --with-xauth=${bindir}/xauth \
                --disable-strip \
                "

# musl doesn't implement wtmp/utmp
EXTRA_OECONF_append_libc-musl = " --disable-wtmp"

# Since we do not depend on libbsd, we do not want configure to use it
# just because it finds libutil.h.  But, specifying --disable-libutil
# causes compile errors, so...
CACHED_CONFIGUREVARS += "ac_cv_header_bsd_libutil_h=no ac_cv_header_libutil_h=no"

# passwd path is hardcoded in sshd
CACHED_CONFIGUREVARS += "ac_cv_path_PATH_PASSWD_PROG=${bindir}/passwd"

# We don't want to depend on libblockfile
CACHED_CONFIGUREVARS += "ac_cv_header_maillock_h=no"

do_configure_prepend () {
	export LD="${CC}"
	install -m 0644 ${WORKDIR}/sshd_config ${B}/
	install -m 0644 ${WORKDIR}/ssh_config ${B}/
}

do_compile_ptest() {
        # skip regress/unittests/ binaries: this will silently skip
        # unittests in run-ptests which is good because they are so slow.
        oe_runmake regress/modpipe regress/setuid-allowed regress/netcat \
                   regress/check-perm regress/mkdtemp
}

do_install_append () {
	if [ "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)}" ]; then
		install -D -m 0644 ${WORKDIR}/sshd ${D}${sysconfdir}/pam.d/sshd
		sed -i -e 's:#UsePAM no:UsePAM yes:' ${D}${sysconfdir}/ssh/sshd_config
	fi

	if [ "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}" ]; then
		sed -i -e 's:#X11Forwarding no:X11Forwarding yes:' ${D}${sysconfdir}/ssh/sshd_config
	fi

	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/sshd
	rm -f ${D}${bindir}/slogin ${D}${datadir}/Ssh.bin
	rmdir ${D}${localstatedir}/run/sshd ${D}${localstatedir}/run ${D}${localstatedir}
	install -d ${D}/${sysconfdir}/default/volatiles
	install -m 644 ${WORKDIR}/volatiles.99_sshd ${D}/${sysconfdir}/default/volatiles/99_sshd
	install -m 0755 ${S}/contrib/ssh-copy-id ${D}${bindir}

	# Create config files for read-only rootfs
	install -d ${D}${sysconfdir}/ssh
	install -m 644 ${D}${sysconfdir}/ssh/sshd_config ${D}${sysconfdir}/ssh/sshd_config_readonly
	sed -i '/HostKey/d' ${D}${sysconfdir}/ssh/sshd_config_readonly
	echo "HostKey /var/run/ssh/ssh_host_rsa_key" >> ${D}${sysconfdir}/ssh/sshd_config_readonly
	echo "HostKey /var/run/ssh/ssh_host_ecdsa_key" >> ${D}${sysconfdir}/ssh/sshd_config_readonly
	echo "HostKey /var/run/ssh/ssh_host_ed25519_key" >> ${D}${sysconfdir}/ssh/sshd_config_readonly

	install -d ${D}${systemd_unitdir}/system
	install -c -m 0644 ${WORKDIR}/sshd.socket ${D}${systemd_unitdir}/system
	install -c -m 0644 ${WORKDIR}/sshd@.service ${D}${systemd_unitdir}/system
	install -c -m 0644 ${WORKDIR}/sshdgenkeys.service ${D}${systemd_unitdir}/system
	sed -i -e 's,@BASE_BINDIR@,${base_bindir},g' \
		-e 's,@SBINDIR@,${sbindir},g' \
		-e 's,@BINDIR@,${bindir},g' \
		-e 's,@LIBEXECDIR@,${libexecdir}/${BPN},g' \
		${D}${systemd_unitdir}/system/sshd.socket ${D}${systemd_unitdir}/system/*.service

	sed -i -e 's,@LIBEXECDIR@,${libexecdir}/${BPN},g' \
		${D}${sysconfdir}/init.d/sshd

	install -D -m 0755 ${WORKDIR}/sshd_check_keys ${D}${libexecdir}/${BPN}/sshd_check_keys
}

do_install_ptest () {
	sed -i -e "s|^SFTPSERVER=.*|SFTPSERVER=${libexecdir}/sftp-server|" regress/test-exec.sh
	cp -r regress ${D}${PTEST_PATH}
}

ALLOW_EMPTY_${PN} = "1"

PACKAGES =+ "${PN}-keygen ${PN}-scp ${PN}-ssh ${PN}-sshd ${PN}-sftp ${PN}-misc ${PN}-sftp-server"
FILES_${PN}-scp = "${bindir}/scp.${BPN}"
FILES_${PN}-ssh = "${bindir}/ssh.${BPN} ${sysconfdir}/ssh/ssh_config"
FILES_${PN}-sshd = "${sbindir}/sshd ${sysconfdir}/init.d/sshd ${systemd_unitdir}/system"
FILES_${PN}-sshd += "${sysconfdir}/ssh/moduli ${sysconfdir}/ssh/sshd_config ${sysconfdir}/ssh/sshd_config_readonly ${sysconfdir}/default/volatiles/99_sshd ${sysconfdir}/pam.d/sshd"
FILES_${PN}-sshd += "${libexecdir}/${BPN}/sshd_check_keys"
FILES_${PN}-sftp = "${bindir}/sftp"
FILES_${PN}-sftp-server = "${libexecdir}/sftp-server"
FILES_${PN}-misc = "${bindir}/ssh* ${libexecdir}/ssh*"
FILES_${PN}-keygen = "${bindir}/ssh-keygen"

RDEPENDS_${PN} += "${PN}-scp ${PN}-ssh ${PN}-sshd ${PN}-keygen"
RDEPENDS_${PN}-sshd += "${PN}-keygen ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam-plugin-keyinit pam-plugin-loginuid', '', d)}"
RRECOMMENDS_${PN}-sshd_append_class-target = "\
    ${@bb.utils.filter('PACKAGECONFIG', 'rng-tools', d)} \
"

# gdb would make attach-ptrace test pass rather than skip but not worth the build dependencies
RDEPENDS_${PN}-ptest += "${PN}-sftp ${PN}-misc ${PN}-sftp-server make sed sudo coreutils"

RPROVIDES_${PN}-ssh = "ssh"
RPROVIDES_${PN}-sshd = "sshd"

RCONFLICTS_${PN} = "dropbear"
RCONFLICTS_${PN}-sshd = "dropbear"

CONFFILES_${PN}-sshd = "${sysconfdir}/ssh/sshd_config"
CONFFILES_${PN}-ssh = "${sysconfdir}/ssh/ssh_config"

ALTERNATIVE_PRIORITY = "90"
ALTERNATIVE_${PN}-scp = "scp"
ALTERNATIVE_${PN}-ssh = "ssh"

BBCLASSEXTEND += "nativesdk"
