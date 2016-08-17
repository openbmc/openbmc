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

inherit update-rc.d update-alternatives

SRC_URI = "http://www.infodrom.org/projects/sysklogd/download/sysklogd-${PV}.tar.gz \
           file://no-strip-install.patch \
           file://0001-Fix-build-with-musl.patch \
           file://sysklogd \
           file://syslog.conf \
           "

SRC_URI_append_e500v2 = " file://no-vectorization.patch"

INITSCRIPT_NAME = "syslog"
CONFFILES_${PN} = "${sysconfdir}/syslog.conf.${BPN}"

EXTRA_OEMAKE = "-e MAKEFLAGS="

CFLAGS_append = " -DSYSV"

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
}

# sysklogd package has no internal systemd support, so we weigh busybox's
# sysklogd utility over it in case of systemd
ALTERNATIVE_PRIORITY = "${@bb.utils.contains('DISTRO_FEATURES','systemd','10','100',d)}"

ALTERNATIVE_${PN} = "syslogd klogd syslog-init syslog-conf"

ALTERNATIVE_LINK_NAME[syslogd] = "${base_sbindir}/syslogd"
ALTERNATIVE_LINK_NAME[klogd] = "${base_sbindir}/klogd"
ALTERNATIVE_LINK_NAME[syslog-init] = "${sysconfdir}/init.d/syslog"
ALTERNATIVE_LINK_NAME[syslog-conf] = "${sysconfdir}/syslog.conf"

pkg_prerm_${PN} () {
	if test "x$D" = "x"; then
	if test "$1" = "upgrade" -o "$1" = "remove"; then
		/etc/init.d/syslog stop
	fi
	fi
}

python () {
    if not bb.utils.contains('DISTRO_FEATURES', 'sysvinit', True, False, d):
        d.setVar("INHIBIT_UPDATERCD_BBCLASS", "1")
}
