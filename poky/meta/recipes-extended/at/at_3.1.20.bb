SUMMARY = "Delayed job execution and batch processing"
HOMEPAGE = "http://blog.calhariz.com/"
DESCRIPTION = "At allows for commands to be run at a particular time.  Batch will execute commands when \
the system load levels drop to a particular level."
SECTION = "base"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4325afd396febcb659c36b49533135d4"
DEPENDS = "flex flex-native bison-native \
           ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)}"

PACKAGECONFIG ?= "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'selinux', '', d)} \
"

PACKAGECONFIG[selinux] = "--with-selinux,--without-selinux,libselinux,"

RDEPENDS_${PN} = "${@bb.utils.contains('DISTRO_FEATURES', 'pam', '${PAM_DEPS}', '', d)} \
"

PAM_DEPS = "libpam libpam-runtime pam-plugin-env pam-plugin-limits"

RCONFLICTS_${PN} = "atd"
RREPLACES_${PN} = "atd"

SRC_URI = "http://snapshot.debian.org/archive/debian/20160728T043443Z/pool/main/a/${BPN}/${BPN}_${PV}.orig.tar.gz \
    file://fix_parallel_build_error.patch \
    file://posixtm.c \
    file://posixtm.h \
    file://file_replacement_with_gplv2.patch \
    file://atd.init \
    file://atd.service \
    ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '${PAM_SRC_URI}', '', d)} \
    file://makefile-fix-parallel.patch \
    file://0001-remove-glibc-assumption.patch \
    "

PAM_SRC_URI = "file://pam.conf.patch \
               file://configure-add-enable-pam.patch"

SRC_URI[md5sum] = "e7bd7b785b2cbb17e133d6bdc0fb099e"
SRC_URI[sha256sum] = "0871923cab73050b98ace020664eb2ddc1e669e4166b5abb64d864d02fcefab9"

UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/a/at/"

EXTRA_OECONF += "ac_cv_path_SENDMAIL=/bin/true \
                 --with-daemon_username=root \
                 --with-daemon_groupname=root \
                 --with-jobdir=/var/spool/at/jobs \
                 --with-atspool=/var/spool/at/spool \
                 ac_cv_header_security_pam_appl_h=${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'yes', 'no', d)} "

inherit autotools-brokensep systemd update-rc.d

INITSCRIPT_NAME = "atd"
INITSCRIPT_PARAMS = "defaults"

SYSTEMD_SERVICE_${PN} = "atd.service"

copy_sources() {
	cp -f ${WORKDIR}/posixtm.[ch] ${S}
}
do_patch[postfuncs] += "copy_sources"

do_install () {
	oe_runmake -e "IROOT=${D}" install

	install -d ${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/atd.init		${D}${sysconfdir}/init.d/atd

	# install systemd unit files
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/atd.service ${D}${systemd_unitdir}/system
	sed -i -e 's,@SBINDIR@,${sbindir},g' ${D}${systemd_unitdir}/system/atd.service

	if [ "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)}" ]; then
		install -D -m 0644 ${WORKDIR}/${BP}/pam.conf ${D}${sysconfdir}/pam.d/atd
	fi
}
