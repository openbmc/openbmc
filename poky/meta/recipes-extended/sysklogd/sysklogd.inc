SUMMARY = "System Log Daemons"
DESCRIPTION = "The sysklogd package implements two system log daemons: syslogd, klogd"
HOMEPAGE = "http://www.infodrom.org/projects/sysklogd/"
SECTION = "base"

LICENSE = "GPLv2+ & BSD"
LICENSE_syslogd = "BSD"
LICENSE_klogd = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b \
                    file://syslogd.c;beginline=2;endline=15;md5=77ffb2fec48c46d7ca0abb2d5813e7fd \
                    file://klogd.c;beginline=2;endline=19;md5=7e87ed0ae6142de079bce738c10c899d \
                   "

inherit update-rc.d update-alternatives systemd

SRC_URI = "http://www.infodrom.org/projects/sysklogd/download/sysklogd-${PV}.tar.gz \
           file://no-strip-install.patch \
           file://0001-Fix-build-with-musl.patch \
           file://0001-fix-problems-that-causes-a-segmentation-fault-under-.patch \
           file://0002-Make-way-for-respecting-flags-from-environment.patch \
           file://sysklogd \
           file://syslog.conf \
           file://syslogd.service \
           file://klogd.service \
           file://tmpfiles.sysklogd.conf \
           "

SRC_URI_append_e500v2 = " file://no-vectorization.patch"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "syslogd.service klogd.service"
SYSTEMD_AUTO_ENABLE = "enable"

INITSCRIPT_NAME = "syslog"
CONFFILES_${PN} = "${sysconfdir}/syslog.conf"
RCONFLICTS_${PN} = "rsyslog busybox-syslog syslog-ng"

CFLAGS += "-DSYSV -D_FILE_OFFSET_BITS=64 -D_LARGEFILE_SOURCE"

do_install () {
	install -d ${D}${mandir}/man8 \
			${D}${mandir}/man5 \
			${D}${base_sbindir}
	oe_runmake 'BINDIR=${D}${base_sbindir}' \
			'MANDIR=${D}${mandir}' install
	install -d ${D}${sysconfdir}
	install -m 644 ${WORKDIR}/syslog.conf ${D}${sysconfdir}/syslog.conf
	install -d ${D}${sysconfdir}/init.d
	install -m 755 ${WORKDIR}/sysklogd ${D}${sysconfdir}/init.d/syslog
	install -d ${D}${systemd_unitdir}/system
	install -m 644 ${WORKDIR}/syslogd.service ${D}${systemd_unitdir}/system
	install -m 644 ${WORKDIR}/klogd.service ${D}${systemd_unitdir}/system
	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true', 'false', d)}; then
		install -d ${D}${exec_prefix}/lib/tmpfiles.d
		install -m 644 ${WORKDIR}/tmpfiles.sysklogd.conf ${D}${exec_prefix}/lib/tmpfiles.d/sysklogd.conf
	fi
}

FILES_${PN} += "${@bb.utils.contains('DISTRO_FEATURES','systemd','${exec_prefix}/lib/tmpfiles.d/sysklogd.conf', '', d)}"

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE_${PN}-doc = "syslogd.8"
ALTERNATIVE_LINK_NAME[syslogd.8] = "${mandir}/man8/syslogd.8"

pkg_prerm_${PN} () {
	if test "x$D" = "x"; then
	if test "$1" = "upgrade" -o "$1" = "remove"; then
		/etc/init.d/syslog stop || :
	fi
	fi
}

python () {
    if not bb.utils.contains('DISTRO_FEATURES', 'sysvinit', True, False, d):
        d.setVar("INHIBIT_UPDATERCD_BBCLASS", "1")
}
