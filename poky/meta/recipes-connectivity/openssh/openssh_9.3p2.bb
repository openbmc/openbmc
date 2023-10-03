SUMMARY = "A suite of security-related network utilities based on \
the SSH protocol including the ssh client and sshd server"
DESCRIPTION = "Secure rlogin/rsh/rcp/telnet replacement (OpenSSH) \
Ssh (Secure Shell) is a program for logging into a remote machine \
and for executing commands on a remote machine."
HOMEPAGE = "http://www.openssh.com/"
SECTION = "console/network"
LICENSE = "BSD-2-Clause & BSD-3-Clause & ISC & MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=072979064e691d342002f43cd89c0394"

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
           file://7280401bdd77ca54be6867a154cc01e0d72612e0.patch \
           "
SRC_URI[sha256sum] = "200ebe147f6cb3f101fd0cdf9e02442af7ddca298dffd9f456878e7ccac676e8"

# This CVE is specific to OpenSSH with the pam opie which we don't build/use here
CVE_CHECK_IGNORE += "CVE-2007-2768"

# This CVE is specific to OpenSSH server, as used in Fedora and Red Hat Enterprise Linux 7
# and when running in a Kerberos environment. As such it is not relevant to OpenEmbedded
CVE_CHECK_IGNORE += "CVE-2014-9278"

# CVE only applies to some distributed RHEL binaries
CVE_CHECK_IGNORE += "CVE-2008-3844"

PAM_SRC_URI = "file://sshd"

inherit manpages useradd update-rc.d update-alternatives systemd

USERADD_PACKAGES = "${PN}-sshd"
USERADD_PARAM:${PN}-sshd = "--system --no-create-home --home-dir /var/run/sshd --shell /bin/false --user-group sshd"
INITSCRIPT_PACKAGES = "${PN}-sshd"
INITSCRIPT_NAME:${PN}-sshd = "sshd"
INITSCRIPT_PARAMS:${PN}-sshd = "defaults 9"

SYSTEMD_PACKAGES = "${PN}-sshd"
SYSTEMD_SERVICE:${PN}-sshd = "sshd.socket"

inherit autotools-brokensep ptest

PACKAGECONFIG ??= ""
PACKAGECONFIG[kerberos] = "--with-kerberos5,--without-kerberos5,krb5"
PACKAGECONFIG[ldns] = "--with-ldns,--without-ldns,ldns"
PACKAGECONFIG[libedit] = "--with-libedit,--without-libedit,libedit"
PACKAGECONFIG[manpages] = "--with-mantype=man,--with-mantype=cat"

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

# musl doesn't implement wtmp/utmp and logwtmp
EXTRA_OECONF:append:libc-musl = " --disable-wtmp --disable-lastlog"

# Since we do not depend on libbsd, we do not want configure to use it
# just because it finds libutil.h.  But, specifying --disable-libutil
# causes compile errors, so...
CACHED_CONFIGUREVARS += "ac_cv_header_bsd_libutil_h=no ac_cv_header_libutil_h=no"

# passwd path is hardcoded in sshd
CACHED_CONFIGUREVARS += "ac_cv_path_PATH_PASSWD_PROG=${bindir}/passwd"

# We don't want to depend on libblockfile
CACHED_CONFIGUREVARS += "ac_cv_header_maillock_h=no"

do_configure:prepend () {
	export LD="${CC}"
	install -m 0644 ${WORKDIR}/sshd_config ${B}/
	install -m 0644 ${WORKDIR}/ssh_config ${B}/
}

do_compile_ptest() {
	oe_runmake regress-binaries regress-unit-binaries
}

do_install:append () {
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

	install -d ${D}${systemd_system_unitdir}
	install -c -m 0644 ${WORKDIR}/sshd.socket ${D}${systemd_system_unitdir}
	install -c -m 0644 ${WORKDIR}/sshd@.service ${D}${systemd_system_unitdir}
	install -c -m 0644 ${WORKDIR}/sshdgenkeys.service ${D}${systemd_system_unitdir}
	sed -i -e 's,@BASE_BINDIR@,${base_bindir},g' \
		-e 's,@SBINDIR@,${sbindir},g' \
		-e 's,@BINDIR@,${bindir},g' \
		-e 's,@LIBEXECDIR@,${libexecdir}/${BPN},g' \
		${D}${systemd_system_unitdir}/sshd.socket ${D}${systemd_system_unitdir}/*.service

	sed -i -e 's,@LIBEXECDIR@,${libexecdir}/${BPN},g' \
		${D}${sysconfdir}/init.d/sshd

	install -D -m 0755 ${WORKDIR}/sshd_check_keys ${D}${libexecdir}/${BPN}/sshd_check_keys
}

do_install_ptest () {
	sed -i -e "s|^SFTPSERVER=.*|SFTPSERVER=${libexecdir}/sftp-server|" regress/test-exec.sh
	cp -r regress ${D}${PTEST_PATH}
	cp config.h ${D}${PTEST_PATH}
}

ALLOW_EMPTY:${PN} = "1"

PACKAGES =+ "${PN}-keygen ${PN}-scp ${PN}-ssh ${PN}-sshd ${PN}-sftp ${PN}-misc ${PN}-sftp-server"
FILES:${PN}-scp = "${bindir}/scp.${BPN}"
FILES:${PN}-ssh = "${bindir}/ssh.${BPN} ${sysconfdir}/ssh/ssh_config"
FILES:${PN}-sshd = "${sbindir}/sshd ${sysconfdir}/init.d/sshd ${systemd_system_unitdir}"
FILES:${PN}-sshd += "${sysconfdir}/ssh/moduli ${sysconfdir}/ssh/sshd_config ${sysconfdir}/ssh/sshd_config_readonly ${sysconfdir}/default/volatiles/99_sshd ${sysconfdir}/pam.d/sshd"
FILES:${PN}-sshd += "${libexecdir}/${BPN}/sshd_check_keys"
FILES:${PN}-sftp = "${bindir}/sftp"
FILES:${PN}-sftp-server = "${libexecdir}/sftp-server"
FILES:${PN}-misc = "${bindir}/ssh* ${libexecdir}/ssh*"
FILES:${PN}-keygen = "${bindir}/ssh-keygen"

RDEPENDS:${PN} += "${PN}-scp ${PN}-ssh ${PN}-sshd ${PN}-keygen ${PN}-sftp-server"
RDEPENDS:${PN}-sshd += "${PN}-keygen ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam-plugin-keyinit pam-plugin-loginuid', '', d)}"
# gdb would make attach-ptrace test pass rather than skip but not worth the build dependencies
RDEPENDS:${PN}-ptest += "${PN}-sftp ${PN}-misc ${PN}-sftp-server make sed sudo coreutils openssl-bin"

RPROVIDES:${PN}-ssh = "ssh"
RPROVIDES:${PN}-sshd = "sshd"

RCONFLICTS:${PN} = "dropbear"
RCONFLICTS:${PN}-sshd = "dropbear"

CONFFILES:${PN}-sshd = "${sysconfdir}/ssh/sshd_config"
CONFFILES:${PN}-ssh = "${sysconfdir}/ssh/ssh_config"

ALTERNATIVE_PRIORITY = "90"
ALTERNATIVE:${PN}-scp = "scp"
ALTERNATIVE:${PN}-ssh = "ssh"

BBCLASSEXTEND += "nativesdk"
