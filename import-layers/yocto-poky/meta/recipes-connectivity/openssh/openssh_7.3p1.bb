SUMMARY = "A suite of security-related network utilities based on \
the SSH protocol including the ssh client and sshd server"
DESCRIPTION = "Secure rlogin/rsh/rcp/telnet replacement (OpenSSH) \
Ssh (Secure Shell) is a program for logging into a remote machine \
and for executing commands on a remote machine."
HOMEPAGE = "http://www.openssh.com/"
SECTION = "console/network"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENCE;md5=e326045657e842541d3f35aada442507"

DEPENDS = "zlib openssl"
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
           file://add-test-support-for-busybox.patch \
           file://run-ptest \
           file://openssh-7.1p1-conditional-compile-des-in-cipher.patch \
           file://openssh-7.1p1-conditional-compile-des-in-pkcs11.patch \
           file://fix-potential-signed-overflow-in-pointer-arithmatic.patch \
           "

PAM_SRC_URI = "file://sshd"

SRC_URI[md5sum] = "dfadd9f035d38ce5d58a3bf130b86d08"
SRC_URI[sha256sum] = "3ffb989a6dcaa69594c3b550d4855a5a2e1718ccdde7f5e36387b424220fbecc"

inherit useradd update-rc.d update-alternatives systemd

USERADD_PACKAGES = "${PN}-sshd"
USERADD_PARAM_${PN}-sshd = "--system --no-create-home --home-dir /var/run/sshd --shell /bin/false --user-group sshd"
INITSCRIPT_PACKAGES = "${PN}-sshd"
INITSCRIPT_NAME_${PN}-sshd = "sshd"
INITSCRIPT_PARAMS_${PN}-sshd = "defaults 9"

SYSTEMD_PACKAGES = "${PN}-sshd"
SYSTEMD_SERVICE_${PN}-sshd = "sshd.socket"

inherit autotools-brokensep ptest

# LFS support:
CFLAGS += "-D__FILE_OFFSET_BITS=64"

# login path is hardcoded in sshd
EXTRA_OECONF = "'LOGIN_PROGRAM=${base_bindir}/login' \
                ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '--with-pam', '--without-pam', d)} \
                --without-zlib-version-check \
                --with-privsep-path=/var/run/sshd \
                --sysconfdir=${sysconfdir}/ssh \
                --with-xauth=/usr/bin/xauth \
                --disable-strip \
                "

# Since we do not depend on libbsd, we do not want configure to use it
# just because it finds libutil.h.  But, specifying --disable-libutil
# causes compile errors, so...
CACHED_CONFIGUREVARS += "ac_cv_header_bsd_libutil_h=no ac_cv_header_libutil_h=no"

# passwd path is hardcoded in sshd
CACHED_CONFIGUREVARS += "ac_cv_path_PATH_PASSWD_PROG=${bindir}/passwd"

# We don't want to depend on libblockfile
CACHED_CONFIGUREVARS += "ac_cv_header_maillock_h=no"

# This is a workaround for uclibc because including stdio.h
# pulls in pthreads.h and causes conflicts in function prototypes.
# This results in compilation failure, so unless this is fixed,
# disable pam for uclibc.
EXTRA_OECONF_append_libc-uclibc=" --without-pam"

do_configure_prepend () {
	export LD="${CC}"
	install -m 0644 ${WORKDIR}/sshd_config ${B}/
	install -m 0644 ${WORKDIR}/ssh_config ${B}/
	if [ ! -e acinclude.m4 -a -e aclocal.m4 ]; then
		cp aclocal.m4 acinclude.m4
	fi
}

do_compile_ptest() {
        # skip regress/unittests/ binaries: this will silently skip
        # unittests in run-ptests which is good because they are so slow.
        oe_runmake regress/modpipe regress/setuid-allowed regress/netcat
}

do_install_append () {
	if [ "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)}" = "pam" ]; then
		install -D -m 0644 ${WORKDIR}/sshd ${D}${sysconfdir}/pam.d/sshd
		sed -i -e 's:#UsePAM no:UsePAM yes:' ${D}${sysconfdir}/ssh/sshd_config
	fi

	if [ "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}" = "x11" ]; then
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
	echo "HostKey /var/run/ssh/ssh_host_dsa_key" >> ${D}${sysconfdir}/ssh/sshd_config_readonly
	echo "HostKey /var/run/ssh/ssh_host_ecdsa_key" >> ${D}${sysconfdir}/ssh/sshd_config_readonly
	echo "HostKey /var/run/ssh/ssh_host_ed25519_key" >> ${D}${sysconfdir}/ssh/sshd_config_readonly

	install -d ${D}${systemd_unitdir}/system
	install -c -m 0644 ${WORKDIR}/sshd.socket ${D}${systemd_unitdir}/system
	install -c -m 0644 ${WORKDIR}/sshd@.service ${D}${systemd_unitdir}/system
	install -c -m 0644 ${WORKDIR}/sshdgenkeys.service ${D}${systemd_unitdir}/system
	sed -i -e 's,@BASE_BINDIR@,${base_bindir},g' \
		-e 's,@SBINDIR@,${sbindir},g' \
		-e 's,@BINDIR@,${bindir},g' \
		${D}${systemd_unitdir}/system/sshd.socket ${D}${systemd_unitdir}/system/*.service
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
FILES_${PN}-sftp = "${bindir}/sftp"
FILES_${PN}-sftp-server = "${libexecdir}/sftp-server"
FILES_${PN}-misc = "${bindir}/ssh* ${libexecdir}/ssh*"
FILES_${PN}-keygen = "${bindir}/ssh-keygen"

RDEPENDS_${PN} += "${PN}-scp ${PN}-ssh ${PN}-sshd ${PN}-keygen"
RDEPENDS_${PN}-sshd += "${PN}-keygen ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam-plugin-keyinit pam-plugin-loginuid', '', d)}"
RDEPENDS_${PN}-ptest += "${PN}-sftp ${PN}-misc ${PN}-sftp-server make"

RPROVIDES_${PN}-ssh = "ssh"
RPROVIDES_${PN}-sshd = "sshd"

RCONFLICTS_${PN} = "dropbear"
RCONFLICTS_${PN}-sshd = "dropbear"
RCONFLICTS_${PN}-keygen = "ssh-keygen"

CONFFILES_${PN}-sshd = "${sysconfdir}/ssh/sshd_config"
CONFFILES_${PN}-ssh = "${sysconfdir}/ssh/ssh_config"

ALTERNATIVE_PRIORITY = "90"
ALTERNATIVE_${PN}-scp = "scp"
ALTERNATIVE_${PN}-ssh = "ssh"

