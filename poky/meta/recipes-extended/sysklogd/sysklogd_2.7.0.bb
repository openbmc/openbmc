SUMMARY = "System Log Daemons"
DESCRIPTION = "The sysklogd package implements system log daemons: syslogd"
HOMEPAGE = "http://www.infodrom.org/projects/sysklogd/"
SECTION = "base"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5b4be4b2549338526758ef479c040943 \
                    file://src/syslogd.c;beginline=2;endline=15;md5=a880fecbc04503f071c494a9c0dd4f97 \
                   "

inherit update-rc.d update-alternatives systemd autotools

SRC_URI = "git://github.com/troglobit/sysklogd.git;branch=master;protocol=https \
           file://sysklogd \
           "

SRCREV = "b45eb487b96935c72fc997d840f82ecf650ed8a5"

S = "${WORKDIR}/git"

EXTRA_OECONF = "--with-systemd=${systemd_system_unitdir} --without-logger"

do_install:append () {
       install -d ${D}${sysconfdir}
       install -m 644 ${S}/syslog.conf ${D}${sysconfdir}/syslog.conf
       install -d ${D}${sysconfdir}/init.d
       install -m 755 ${UNPACKDIR}/sysklogd ${D}${sysconfdir}/init.d/syslog
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "syslogd.service"
SYSTEMD_AUTO_ENABLE = "enable"

INITSCRIPT_NAME = "syslog"
CONFFILES:${PN} = "${sysconfdir}/syslog.conf"
RCONFLICTS:${PN} = "rsyslog busybox-syslog syslog-ng"

FILES:${PN} += "${@bb.utils.contains('DISTRO_FEATURES','systemd','${exec_prefix}/lib/tmpfiles.d/sysklogd.conf', '', d)}"

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE:${PN}-doc = "syslogd.8"
ALTERNATIVE_LINK_NAME[syslogd.8] = "${mandir}/man8/syslogd.8"

pkg_prerm:${PN} () {
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
