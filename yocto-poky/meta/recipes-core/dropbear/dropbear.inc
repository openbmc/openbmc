SUMMARY = "A lightweight SSH and SCP implementation"
HOMEPAGE = "http://matt.ucc.asn.au/dropbear/dropbear.html"
SECTION = "console/network"

# some files are from other projects and have others license terms:
#   public domain, OpenSSH 3.5p1, OpenSSH3.6.1p2, PuTTY
LICENSE = "MIT & BSD-3-Clause & BSD-2-Clause & PD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a5ec40cafba26fc4396d0b550f824e01"

DEPENDS = "zlib"
RPROVIDES_${PN} = "ssh sshd" 

DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)}"

SRC_URI = "http://matt.ucc.asn.au/dropbear/releases/dropbear-${PV}.tar.bz2 \
           file://0001-urandom-xauth-changes-to-options.h.patch \
           file://0003-configure.patch \
           file://0004-fix-2kb-keys.patch \
           file://0007-dropbear-fix-for-x32-abi.patch \
           file://init \
           file://dropbearkey.service \
           file://dropbear@.service \
           file://dropbear.socket \
           ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '${PAM_SRC_URI}', '', d)} "

PAM_SRC_URI = "file://0005-dropbear-enable-pam.patch \
               file://0006-dropbear-configuration-file.patch \
               file://dropbear"

PAM_PLUGINS = "libpam-runtime \
	pam-plugin-deny \
	pam-plugin-permit \
	pam-plugin-unix \
	"
RDEPENDS_${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'pam', '${PAM_PLUGINS}', '', d)}"

inherit autotools update-rc.d systemd

INITSCRIPT_NAME = "dropbear"
INITSCRIPT_PARAMS = "defaults 10"

SYSTEMD_SERVICE_${PN} = "dropbear.socket"

SBINCOMMANDS = "dropbear dropbearkey dropbearconvert"
BINCOMMANDS = "dbclient ssh scp"
EXTRA_OEMAKE = 'MULTI=1 SCPPROGRESS=1 PROGRAMS="${SBINCOMMANDS} ${BINCOMMANDS}"'
EXTRA_OECONF += "\
 ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '--enable-pam', '--disable-pam', d)}"
CFLAGS += "-DSFTPSERVER_PATH=\\"${libdir}/openssh/sftp-server\\""

do_install() {
	install -d ${D}${sysconfdir} \
		${D}${sysconfdir}/init.d \
		${D}${sysconfdir}/default \
		${D}${sysconfdir}/dropbear \
		${D}${bindir} \
		${D}${sbindir} \
		${D}${localstatedir}

	install -m 0755 dropbearmulti ${D}${sbindir}/
	ln -s ${sbindir}/dropbearmulti ${D}${bindir}/dbclient
	
	for i in ${SBINCOMMANDS}
	do
		ln -s ./dropbearmulti ${D}${sbindir}/$i
	done
	sed -e 's,/etc,${sysconfdir},g' \
		-e 's,/usr/sbin,${sbindir},g' \
		-e 's,/var,${localstatedir},g' \
		-e 's,/usr/bin,${bindir},g' \
		-e 's,/usr,${prefix},g' ${WORKDIR}/init > ${D}${sysconfdir}/init.d/dropbear
	chmod 755 ${D}${sysconfdir}/init.d/dropbear
	if [ "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)}" = "pam" ]; then
		install -d ${D}${sysconfdir}/pam.d
		install -m 0644 ${WORKDIR}/dropbear  ${D}${sysconfdir}/pam.d/
	fi

	# deal with systemd unit files
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/dropbearkey.service ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/dropbear@.service ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/dropbear.socket ${D}${systemd_unitdir}/system
	sed -i -e 's,@BASE_BINDIR@,${base_bindir},g' \
		-e 's,@BINDIR@,${bindir},g' \
		-e 's,@SBINDIR@,${sbindir},g' \
		${D}${systemd_unitdir}/system/dropbear.socket ${D}${systemd_unitdir}/system/*.service
}

inherit update-alternatives

ALTERNATIVE_PRIORITY = "20"
ALTERNATIVE_${PN} = "scp ssh"

ALTERNATIVE_TARGET = "${sbindir}/dropbearmulti"

pkg_postrm_append_${PN} () {
  if [ -f "${sysconfdir}/dropbear/dropbear_rsa_host_key" ]; then
        rm ${sysconfdir}/dropbear/dropbear_rsa_host_key
  fi
  if [ -f "${sysconfdir}/dropbear/dropbear_dss_host_key" ]; then
        rm ${sysconfdir}/dropbear/dropbear_dss_host_key
  fi
}
