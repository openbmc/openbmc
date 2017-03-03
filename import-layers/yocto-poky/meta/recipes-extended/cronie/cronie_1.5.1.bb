SUMMARY = "Cron daemon for executing programs at set times"
DESCRIPTION = "Cronie contains the standard UNIX daemon crond that runs \
specified programs at scheduled times and related tools. It is based on the \
original cron and has security and configuration enhancements like the \
ability to use pam and SELinux."
HOMEPAGE = "https://fedorahosted.org/cronie/"
BUGTRACKER = "https://bugzilla.redhat.com"

# Internet Systems Consortium License
LICENSE = "ISC & BSD-3-Clause & BSD-2-Clause & GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=dd2a592170760e1386c769e1043b3722 \
                    file://src/cron.c;endline=20;md5=b425c334265026177128353a142633b4 \
                    file://src/popen.c;beginline=3;endline=31;md5=edd50742d8def712e9472dba353668a9"

SECTION = "utils"

SRC_URI = "https://fedorahosted.org/releases/c/r/cronie/cronie-${PV}.tar.gz \
           file://crond.init \
           file://crontab \
           file://crond.service \
           ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '${PAM_SRC_URI}', '', d)}"

PAM_SRC_URI = "file://crond_pam_config.patch"
PAM_DEPS = "libpam libpam-runtime pam-plugin-access pam-plugin-loginuid"

SRC_URI[md5sum] = "910e6effcc032998b0a49fbd46322e18"
SRC_URI[sha256sum] = "6c73666102a6b5d35e2eaf1bd06925f2d4b0cef8d3323c37286dda3089a85338"

inherit autotools update-rc.d useradd systemd

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)}"

PACKAGECONFIG[audit] = "--with-audit,--without-audit,audit,"
PACKAGECONFIG[pam] = "--with-pam,--without-pam,libpam,${PAM_DEPS}"

INITSCRIPT_NAME = "crond"
INITSCRIPT_PARAMS = "start 90 2 3 4 5 . stop 60 0 1 6 ."

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "--system crontab"

SYSTEMD_SERVICE_${PN} = "crond.service"

do_install_append () {
	install -d ${D}${sysconfdir}/sysconfig/
	install -d ${D}${sysconfdir}/init.d/
	install -m 0644 ${S}/crond.sysconfig ${D}${sysconfdir}/sysconfig/crond
	install -m 0755 ${WORKDIR}/crond.init ${D}${sysconfdir}/init.d/crond

	# install systemd unit files
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/crond.service ${D}${systemd_unitdir}/system
	sed -i -e 's,@BASE_BINDIR@,${base_bindir},g' \
	       -e 's,@SBINDIR@,${sbindir},g' \
	       ${D}${systemd_unitdir}/system/crond.service

	# below are necessary for a complete cron environment
	install -d ${D}${localstatedir}/spool/cron
	install -m 0755 ${WORKDIR}/crontab ${D}${sysconfdir}/
	mkdir -p ${D}${sysconfdir}/cron.d
	mkdir -p ${D}${sysconfdir}/cron.hourly
	mkdir -p ${D}${sysconfdir}/cron.daily
	mkdir -p ${D}${sysconfdir}/cron.weekly
	mkdir -p ${D}${sysconfdir}/cron.monthly
	touch ${D}${sysconfdir}/cron.deny

	# below setting is necessary to allow normal user using crontab

	# setgid for crontab binary
	chown root:crontab ${D}${bindir}/crontab
	chmod 2755 ${D}${bindir}/crontab

	# allow 'crontab' group write to /var/spool/cron
	chown root:crontab ${D}${localstatedir}/spool/cron
	chmod 770 ${D}${localstatedir}/spool/cron

	chmod 600 ${D}${sysconfdir}/crontab
}

FILES_${PN} += "${sysconfdir}/cron*"
CONFFILES_${PN} += "${sysconfdir}/crontab"
